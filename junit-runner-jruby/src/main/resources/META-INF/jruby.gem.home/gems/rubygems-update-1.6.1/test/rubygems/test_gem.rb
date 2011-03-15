require 'rubygems/test_case'
require 'rubygems'
require 'rubygems/gem_openssl'
require 'rubygems/installer'
require 'pathname'
require 'tmpdir'

class TestGem < Gem::TestCase

  def setup
    super

    @additional = %w[a b].map { |d| File.join @tempdir, d }
    @default_dir_re = if RUBY_VERSION > '1.9' then
                        %r|/.*?[Rr]uby.*?/[Gg]ems/[0-9.]+|
                      else
                        %r|/[Rr]uby/[Gg]ems/[0-9.]+|
                      end

    util_remove_interrupt_command
  end

  def assert_activate expected, *specs
    specs.each do |spec|
      case spec
      when Array
        Gem.activate(*spec)
      when String
        Gem.activate spec
      else
        Gem.activate spec.name
      end
    end

    loaded = Gem.loaded_specs.values.map(&:full_name)

    assert_equal expected.sort, loaded.sort if expected
  end

  def test_self_activate
    foo = util_spec 'foo', '1'

    assert_activate %w[foo-1], foo
  end

  def loaded_spec_names
    Gem.loaded_specs.values.map(&:full_name).sort
  end

  def unresolved_names
    Gem.unresolved_deps.values.map(&:to_s).sort
  end

  def test_self_activate_via_require
    new_spec "a", "1", "b" => "= 1"
    new_spec "b", "1", nil, "lib/b/c.rb"
    new_spec "b", "2", nil, "lib/b/c.rb"

    Gem.activate "a", "= 1"
    require "b/c"

    assert_equal %w(a-1 b-1), loaded_spec_names
  end

  def test_self_activate_deep_unambiguous
    a1 = new_spec "a", "1", "b" => "= 1"
    b1 = new_spec "b", "1", "c" => "= 1"
    b2 = new_spec "b", "2", "c" => "= 2"
    c1 = new_spec "c", "1"
    c2 = new_spec "c", "2"

    install_specs a1, b1, b2, c1, c2

    Gem.activate "a", "= 1"
    assert_equal %w(a-1 b-1 c-1), loaded_spec_names
  end

  def save_loaded_features
    old_loaded_features = $LOADED_FEATURES.dup
    yield
  ensure
    $LOADED_FEATURES.replace old_loaded_features
  end

  def test_self_activate_ambiguous_direct
    save_loaded_features do
      a1 = new_spec "a", "1", "b" => "> 0"
      b1 = new_spec("b", "1", { "c" => ">= 1" }, "lib/d.rb")
      b2 = new_spec("b", "2", { "c" => ">= 2" }, "lib/d.rb")
      c1 = new_spec "c", "1"
      c2 = new_spec "c", "2"

      install_specs a1, b1, b2, c1, c2

      Gem.activate "a", "= 1"
      assert_equal %w(a-1), loaded_spec_names
      assert_equal ["b (> 0)"], unresolved_names

      require "d"

      assert_equal %w(a-1 b-2 c-2), loaded_spec_names
      assert_equal [], unresolved_names
    end
  end

  def test_self_activate_ambiguous_indirect
    save_loaded_features do
      a1 = new_spec "a", "1", "b" => "> 0"
      b1 = new_spec "b", "1", "c" => ">= 1"
      b2 = new_spec "b", "2", "c" => ">= 2"
      c1 = new_spec "c", "1", nil, "lib/d.rb"
      c2 = new_spec "c", "2", nil, "lib/d.rb"

      install_specs a1, b1, b2, c1, c2

      Gem.activate "a", "= 1"
      assert_equal %w(a-1), loaded_spec_names
      assert_equal ["b (> 0)"], unresolved_names

      require "d"

      assert_equal %w(a-1 b-2 c-2), loaded_spec_names
      assert_equal [], unresolved_names
    end
  end

  def test_self_activate_ambiguous_unrelated
    save_loaded_features do
      a1 = new_spec "a", "1", "b" => "> 0"
      b1 = new_spec "b", "1", "c" => ">= 1"
      b2 = new_spec "b", "2", "c" => ">= 2"
      c1 = new_spec "c", "1"
      c2 = new_spec "c", "2"
      d1 = new_spec "d", "1", nil, "lib/d.rb"

      install_specs a1, b1, b2, c1, c2, d1

      Gem.activate "a", "= 1"
      assert_equal %w(a-1), loaded_spec_names
      assert_equal ["b (> 0)"], unresolved_names

      require "d"

      assert_equal %w(a-1 d-1), loaded_spec_names
      assert_equal ["b (> 0)"], unresolved_names
    end
  end

  def test_self_activate_ambiguous_indirect_conflict
    save_loaded_features do
      a1 = new_spec "a", "1", "b" => "> 0"
      a2 = new_spec "a", "2", "b" => "> 0"
      b1 = new_spec "b", "1", "c" => ">= 1"
      b2 = new_spec "b", "2", "c" => ">= 2"
      c1 = new_spec "c", "1", nil, "lib/d.rb"
      c2 = new_spec("c", "2", { "a" => "1" }, "lib/d.rb") # conflicts with a-2

      install_specs a1, a2, b1, b2, c1, c2

      Gem.activate "a", "= 2"
      assert_equal %w(a-2), loaded_spec_names
      assert_equal ["b (> 0)"], unresolved_names

      require "d"

      assert_equal %w(a-2 b-1 c-1), loaded_spec_names
      assert_equal [], unresolved_names
    end
  end

  def test_require_missing
    save_loaded_features do
      assert_raises ::LoadError do
        require "q"
      end
    end
  end

  def test_self_activate_loaded
    util_spec 'foo', '1'

    assert Gem.activate 'foo'
    refute Gem.activate 'foo'
  end

  ##
  # [A] depends on
  #     [B] >= 1.0 (satisfied by 2.0)
  # [C] depends on nothing

  def test_self_activate_unrelated
    a = util_spec 'a', '1.0', 'b' => '>= 1.0'
        util_spec 'b', '1.0'
    c = util_spec 'c', '1.0'

    assert_activate %w[b-1.0 c-1.0 a-1.0], a, c, "b"
  end

  ##
  # [A] depends on
  #     [B] >= 1.0 (satisfied by 2.0)
  #     [C]  = 1.0 depends on
  #         [B] ~> 1.0
  #
  # and should resolve using b-1.0

  def test_self_activate_over
    util_spec 'a', '1.0', 'b' => '>= 1.0', 'c' => '= 1.0'
    util_spec 'b', '1.0'
    util_spec 'b', '1.1'
    util_spec 'b', '2.0'
    util_spec 'c', '1.0', 'b' => '~> 1.0'

    Gem.activate "a"

    assert_equal %w[a-1.0 c-1.0], loaded_spec_names
    assert_equal ["b (>= 1.0, ~> 1.0)"], unresolved_names
  end

  ##
  # [A] depends on
  #     [B] ~> 1.0 (satisfied by 1.1)
  #     [C]  = 1.0 depends on
  #         [B] = 1.0
  #
  # and should resolve using b-1.0
  #
  # TODO: this is not under, but over... under would require depth
  # first resolve through a dependency that is later pruned.

  def test_self_activate_under
    a,   _ = util_spec 'a', '1.0', 'b' => '~> 1.0', 'c' => '= 1.0'
             util_spec 'b', '1.0'
             util_spec 'b', '1.1'
    c,   _ = util_spec 'c', '1.0', 'b' => '= 1.0'

    assert_activate %w[b-1.0 c-1.0 a-1.0], a, c, "b"
  end

  ##
  # [A1] depends on
  #    [B] > 0 (satisfied by 2.0)
  # [B1] depends on
  #    [C] > 0 (satisfied by 1.0)
  # [B2] depends on nothing!
  # [C1] depends on nothing

  def test_self_activate_dropped
    a1, = util_spec 'a', '1', 'b' => nil
          util_spec 'b', '1', 'c' => nil
          util_spec 'b', '2'
          util_spec 'c', '1'

    assert_activate %w[b-2 a-1], a1, "b"
  end

  ##
  # [A] depends on
  #     [B] >= 1.0 (satisfied by 1.1) depends on
  #         [Z]
  #     [C] >= 1.0 depends on
  #         [B] = 1.0
  #
  # and should backtrack to resolve using b-1.0, pruning Z from the
  # resolve.

  def test_self_activate_raggi_the_edgecase_generator
    a,  _ = util_spec 'a', '1.0', 'b' => '>= 1.0', 'c' => '>= 1.0'
            util_spec 'b', '1.0'
            util_spec 'b', '1.1', 'z' => '>= 1.0'
    c,  _ = util_spec 'c', '1.0', 'b' => '= 1.0'

    assert_activate %w[b-1.0 c-1.0 a-1.0], a, c, "b"
  end

  def test_self_activate_conflict
    util_spec 'b', '1.0'
    util_spec 'b', '2.0'

    gem "b", "= 1.0"

    assert_raises Gem::LoadError do
      gem "b", "= 2.0"
    end
  end

  ##
  # [A] depends on
  #     [B] ~> 1.0 (satisfied by 1.0)
  #     [C]  = 1.0 depends on
  #         [B] = 2.0

  def test_self_activate_divergent
    a, _  = util_spec 'a', '1.0', 'b' => '~> 1.0', 'c' => '= 1.0'
            util_spec 'b', '1.0'
            util_spec 'b', '2.0'
    c,  _ = util_spec 'c', '1.0', 'b' => '= 2.0'

    e = assert_raises Gem::LoadError do
      assert_activate nil, a, c, "b"
    end

    assert_match(/Unable to activate c-1.0,/, e.message)
    assert_match(/because b-1.0 conflicts with b .= 2.0/, e.message)
  end

  ##
  # DOC

  def test_self_activate_platform_alternate
    @x1_m = util_spec 'x', '1' do |s|
      s.platform = Gem::Platform.new %w[cpu my_platform 1]
    end

    @x1_o = util_spec 'x', '1' do |s|
      s.platform = Gem::Platform.new %w[cpu other_platform 1]
    end

    @w1 = util_spec 'w', '1', 'x' => nil

    util_set_arch 'cpu-my_platform1'

    assert_activate %w[x-1-cpu-my_platform-1 w-1], @w1, @x1_m
  end

  ##
  # DOC

  def test_self_activate_platform_bump
    @y1 = util_spec 'y', '1'

    @y1_1_p = util_spec 'y', '1.1' do |s|
      s.platform = Gem::Platform.new %w[cpu my_platform 1]
    end

    @z1 = util_spec 'z', '1', 'y' => nil

    assert_activate %w[y-1 z-1], @z1, @y1
  end

  ##
  # [C] depends on
  #     [A] = 1.a
  #     [B] = 1.0 depends on
  #         [A] >= 0 (satisfied by 1.a)

  def test_self_activate_prerelease
    @c1_pre = util_spec 'c', '1.a', "a" => "1.a", "b" => "1"
    @a1_pre = util_spec 'a', '1.a'
    @b1     = util_spec 'b', '1' do |s|
      s.add_dependency 'a'
      s.add_development_dependency 'aa'
    end

    assert_activate %w[a-1.a b-1 c-1.a], @c1_pre, @a1_pre, @b1
  end

  ##
  # DOC

  def test_self_activate_old_required
    e1, = util_spec 'e', '1', 'd' => '= 1'
    @d1 = util_spec 'd', '1'
    @d2 = util_spec 'd', '2'

    assert_activate %w[d-1 e-1], e1, "d"
  end

  def test_self_all_load_paths
    util_make_gems

    expected = [
      File.join(@gemhome, *%W[gems #{@a1.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@a2.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@a3a.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@a_evil9.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@b2.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@c1_2.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@pl1.full_name} lib]),
    ]

    assert_equal expected, Gem.all_load_paths.sort
  end

  def test_self_available?
    util_make_gems
    assert(Gem.available?("a"))
    assert(Gem.available?("a", "1"))
    assert(Gem.available?("a", ">1"))
    assert(!Gem.available?("monkeys"))
  end

  def test_self_bin_path_bin_name
    util_exec_gem
    assert_equal @abin_path, Gem.bin_path('a', 'abin')
  end

  def test_self_bin_path_bin_name_version
    util_exec_gem
    assert_equal @abin_path, Gem.bin_path('a', 'abin', '4')
  end

  def test_self_bin_path_name
    util_exec_gem
    assert_equal @exec_path, Gem.bin_path('a')
  end

  def test_self_bin_path_name_version
    util_exec_gem
    assert_equal @exec_path, Gem.bin_path('a', nil, '4')
  end

  def test_self_bin_path_nonexistent_binfile
    quick_spec 'a', '2' do |s|
      s.executables = ['exec']
    end
    assert_raises(Gem::GemNotFoundException) do
      Gem.bin_path('a', 'other', '2')
    end
  end

  def test_self_bin_path_no_bin_file
    quick_spec 'a', '1'
    assert_raises(Gem::Exception) do
      Gem.bin_path('a', nil, '1')
    end
  end

  def test_self_bin_path_not_found
    assert_raises(Gem::GemNotFoundException) do
      Gem.bin_path('non-existent')
    end
  end

  def test_self_bin_path_bin_file_gone_in_latest
    util_exec_gem
    quick_spec 'a', '10' do |s|
      s.executables = []
      s.default_executable = nil
    end
    # Should not find a-10's non-abin (bug)
    assert_equal @abin_path, Gem.bin_path('a', 'abin')
  end

  def test_self_bindir
    assert_equal File.join(@gemhome, 'bin'), Gem.bindir
    assert_equal File.join(@gemhome, 'bin'), Gem.bindir(Gem.dir)
    assert_equal File.join(@gemhome, 'bin'), Gem.bindir(Pathname.new(Gem.dir))
  end

  def test_self_bindir_default_dir
    default = Gem.default_dir
    bindir = if defined?(RUBY_FRAMEWORK_VERSION) then
               '/usr/bin'
             else
               RbConfig::CONFIG['bindir']
             end

    assert_equal bindir, Gem.bindir(default)
    assert_equal bindir, Gem.bindir(Pathname.new(default))
  end

  def test_self_clear_paths
    Gem.dir
    Gem.path
    searcher = Gem.searcher
    source_index = Gem.source_index

    Gem.clear_paths

    assert_equal nil, Gem.instance_variable_get(:@gem_home)
    assert_equal nil, Gem.instance_variable_get(:@gem_path)
    refute_equal searcher, Gem.searcher
    refute_equal source_index.object_id, Gem.source_index.object_id
  end

  def test_self_configuration
    expected = Gem::ConfigFile.new []
    Gem.configuration = nil

    assert_equal expected, Gem.configuration
  end

  def test_self_datadir
    foo = nil

    Dir.chdir @tempdir do
      FileUtils.mkdir_p 'data'
      File.open File.join('data', 'foo.txt'), 'w' do |fp|
        fp.puts 'blah'
      end

      foo = quick_spec 'foo' do |s| s.files = %w[data/foo.txt] end
      install_gem foo
    end

    Gem.source_index = nil

    gem 'foo'

    expected = File.join @gemhome, 'gems', foo.full_name, 'data', 'foo'

    assert_equal expected, Gem.datadir('foo')
  end

  def test_self_datadir_nonexistent_package
    assert_nil Gem.datadir('xyzzy')
  end

  def test_self_default_dir
    assert_match @default_dir_re, Gem.default_dir
  end

  def test_self_default_exec_format
    orig_RUBY_INSTALL_NAME = Gem::ConfigMap[:ruby_install_name]
    Gem::ConfigMap[:ruby_install_name] = 'ruby'

    assert_equal '%s', Gem.default_exec_format
  ensure
    Gem::ConfigMap[:ruby_install_name] = orig_RUBY_INSTALL_NAME
  end

  def test_self_default_exec_format_18
    orig_RUBY_INSTALL_NAME = Gem::ConfigMap[:ruby_install_name]
    Gem::ConfigMap[:ruby_install_name] = 'ruby18'

    assert_equal '%s18', Gem.default_exec_format
  ensure
    Gem::ConfigMap[:ruby_install_name] = orig_RUBY_INSTALL_NAME
  end

  def test_self_default_exec_format_jruby
    orig_RUBY_INSTALL_NAME = Gem::ConfigMap[:ruby_install_name]
    Gem::ConfigMap[:ruby_install_name] = 'jruby'

    assert_equal 'j%s', Gem.default_exec_format
  ensure
    Gem::ConfigMap[:ruby_install_name] = orig_RUBY_INSTALL_NAME
  end

  def test_self_default_sources
    assert_equal %w[http://rubygems.org/], Gem.default_sources
  end

  def test_self_dir
    assert_equal @gemhome, Gem.dir
  end

  def test_self_ensure_gem_directories
    FileUtils.rm_r @gemhome
    Gem.use_paths @gemhome

    Gem.ensure_gem_subdirectories @gemhome

    assert File.directory?(Gem.cache_dir(@gemhome))
  end

  def test_self_ensure_gem_directories_missing_parents
    gemdir = File.join @tempdir, 'a/b/c/gemdir'
    FileUtils.rm_rf File.join(@tempdir, 'a') rescue nil
    refute File.exist?(File.join(@tempdir, 'a')),
           "manually remove #{File.join @tempdir, 'a'}, tests are broken"
    Gem.use_paths gemdir

    Gem.ensure_gem_subdirectories gemdir

    assert File.directory?(Gem.cache_dir(gemdir))
  end

  unless win_platform? then # only for FS that support write protection
    def test_self_ensure_gem_directories_write_protected
      gemdir = File.join @tempdir, "egd"
      FileUtils.rm_r gemdir rescue nil
      refute File.exist?(gemdir), "manually remove #{gemdir}, tests are broken"
      FileUtils.mkdir_p gemdir
      FileUtils.chmod 0400, gemdir
      Gem.use_paths gemdir

      Gem.ensure_gem_subdirectories gemdir

      refute File.exist?(Gem.cache_dir(gemdir))
    ensure
      FileUtils.chmod 0600, gemdir
    end

    def test_self_ensure_gem_directories_write_protected_parents
      parent = File.join(@tempdir, "egd")
      gemdir = "#{parent}/a/b/c"

      FileUtils.rm_r parent rescue nil
      refute File.exist?(parent), "manually remove #{parent}, tests are broken"
      FileUtils.mkdir_p parent
      FileUtils.chmod 0400, parent
      Gem.use_paths(gemdir)

      Gem.ensure_gem_subdirectories gemdir

      refute File.exist?(Gem.cache_dir(gemdir))
    ensure
      FileUtils.chmod 0600, parent
    end
  end

  def test_ensure_ssl_available
    orig_Gem_ssl_available = Gem.ssl_available?

    Gem.ssl_available = true
    Gem.ensure_ssl_available

    Gem.ssl_available = false
    e = assert_raises Gem::Exception do Gem.ensure_ssl_available end
    assert_equal 'SSL is not installed on this system', e.message
  ensure
    Gem.ssl_available = orig_Gem_ssl_available
  end

  def test_self_find_files
    discover_path = File.join 'lib', 'sff', 'discover.rb'
    cwd = File.expand_path("test/rubygems", @@project_dir)
    $LOAD_PATH.unshift cwd

    foo1 = quick_gem 'sff', '1' do |s|
      s.files << discover_path
    end

    foo2 = quick_gem 'sff', '2' do |s|
      s.files << discover_path
    end

    path = File.join 'gems', foo1.full_name, discover_path
    write_file(path) { |fp| fp.puts "# #{path}" }

    path = File.join 'gems', foo2.full_name, discover_path
    write_file(path) { |fp| fp.puts "# #{path}" }

    @fetcher = Gem::FakeFetcher.new
    Gem::RemoteFetcher.fetcher = @fetcher

    Gem.source_index = util_setup_spec_fetcher foo1, foo2

    Gem.searcher = nil

    expected = [
      File.expand_path('test/rubygems/sff/discover.rb', @@project_dir),
      File.join(foo2.full_gem_path, discover_path),
      File.join(foo1.full_gem_path, discover_path),
    ]

    assert_equal expected, Gem.find_files('sff/discover')
    assert_equal expected, Gem.find_files('sff/**.rb'), '[ruby-core:31730]'
  ensure
    assert_equal cwd, $LOAD_PATH.shift
  end

  def test_self_latest_load_paths
    util_make_gems

    expected = [
      File.join(@gemhome, *%W[gems #{@a3a.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@a_evil9.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@b2.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@c1_2.full_name} lib]),
      File.join(@gemhome, *%W[gems #{@pl1.full_name} lib]),
    ]

    assert_equal expected, Gem.latest_load_paths.sort
  end

  def test_self_loaded_specs
    foo = quick_spec 'foo'
    install_gem foo
    Gem.source_index = nil

    Gem.activate 'foo'

    assert_equal true, Gem.loaded_specs.keys.include?('foo')
  end

  def util_path
    ENV.delete "GEM_HOME"
    ENV.delete "GEM_PATH"
  end

  def test_self_path
    assert_equal [Gem.dir], Gem.path
  end

  def test_self_path_default
    util_path

    if defined? APPLE_GEM_HOME
      orig_APPLE_GEM_HOME = APPLE_GEM_HOME
      Object.send :remove_const, :APPLE_GEM_HOME
    end
    Gem.instance_variable_set :@gem_path, nil

    assert_equal [Gem.default_path, Gem.dir].flatten, Gem.path
  ensure
    Object.const_set :APPLE_GEM_HOME, orig_APPLE_GEM_HOME
  end

  unless win_platform?
    def test_self_path_APPLE_GEM_HOME
      util_path

      Gem.clear_paths
      apple_gem_home = File.join @tempdir, 'apple_gem_home'
      Gem.const_set :APPLE_GEM_HOME, apple_gem_home

      assert_includes Gem.path, apple_gem_home
    ensure
      Gem.send :remove_const, :APPLE_GEM_HOME
    end

    def test_self_path_APPLE_GEM_HOME_GEM_PATH
      Gem.clear_paths
      ENV['GEM_PATH'] = @gemhome
      apple_gem_home = File.join @tempdir, 'apple_gem_home'
      Gem.const_set :APPLE_GEM_HOME, apple_gem_home

      refute Gem.path.include?(apple_gem_home)
    ensure
      Gem.send :remove_const, :APPLE_GEM_HOME
    end
  end

  def test_self_path_ENV_PATH
    Gem.send :set_paths, nil
    path_count = Gem.path.size
    Gem.clear_paths

    ENV['GEM_PATH'] = @additional.join(File::PATH_SEPARATOR)

    assert_equal @additional, Gem.path[0,2]

    assert_equal path_count + @additional.size, Gem.path.size,
                 "extra path components: #{Gem.path[2..-1].inspect}"
    assert_equal Gem.dir, Gem.path.last
  end

  def test_self_path_duplicate
    Gem.clear_paths
    util_ensure_gem_dirs
    dirs = @additional + [@gemhome] + [File.join(@tempdir, 'a')]

    ENV['GEM_HOME'] = @gemhome
    ENV['GEM_PATH'] = dirs.join File::PATH_SEPARATOR

    assert_equal @gemhome, Gem.dir

    paths = [Gem.dir]
    assert_equal @additional + paths, Gem.path
  end

  def test_self_path_overlap
    Gem.clear_paths

    util_ensure_gem_dirs
    ENV['GEM_HOME'] = @gemhome
    ENV['GEM_PATH'] = @additional.join(File::PATH_SEPARATOR)

    assert_equal @gemhome, Gem.dir

    paths = [Gem.dir]
    assert_equal @additional + paths, Gem.path
  end

  def test_self_platforms
    assert_equal [Gem::Platform::RUBY, Gem::Platform.local], Gem.platforms
  end

  def test_self_prefix
    assert_equal @@project_dir, Gem.prefix
  end

  def test_self_prefix_libdir
    orig_libdir = Gem::ConfigMap[:libdir]
    Gem::ConfigMap[:libdir] = @@project_dir

    assert_nil Gem.prefix
  ensure
    Gem::ConfigMap[:libdir] = orig_libdir
  end

  def test_self_prefix_sitelibdir
    orig_sitelibdir = Gem::ConfigMap[:sitelibdir]
    Gem::ConfigMap[:sitelibdir] = @@project_dir

    assert_nil Gem.prefix
  ensure
    Gem::ConfigMap[:sitelibdir] = orig_sitelibdir
  end

  def test_self_refresh
    util_make_gems

    a1_spec = File.join @gemhome, "specifications", @a1.spec_name

    FileUtils.mv a1_spec, @tempdir

    refute Gem.source_index.gems.include?(@a1.full_name)

    FileUtils.mv File.join(@tempdir, @a1.spec_name), a1_spec

    Gem.refresh

    assert_includes Gem.source_index.gems, @a1.full_name
    assert_equal nil, Gem.instance_variable_get(:@searcher)
  end

  def test_self_required_location
    util_make_gems

    assert_equal File.join(@tempdir, *%w[gemhome gems c-1.2 lib code.rb]),
                 Gem.required_location("c", "code.rb")
    assert_equal File.join(@tempdir, *%w[gemhome gems a-1 lib code.rb]),
                 Gem.required_location("a", "code.rb", "< 2")
    assert_equal File.join(@tempdir, *%w[gemhome gems a-2 lib code.rb]),
                 Gem.required_location("a", "code.rb", "= 2")
  end

  def test_self_ruby_escaping_spaces_in_path
    orig_ruby = Gem.ruby
    orig_bindir = Gem::ConfigMap[:bindir]
    orig_ruby_install_name = Gem::ConfigMap[:ruby_install_name]
    orig_exe_ext = Gem::ConfigMap[:EXEEXT]

    Gem::ConfigMap[:bindir] = "C:/Ruby 1.8/bin"
    Gem::ConfigMap[:ruby_install_name] = "ruby"
    Gem::ConfigMap[:EXEEXT] = ".exe"
    Gem.instance_variable_set("@ruby", nil)

    assert_equal "\"C:/Ruby 1.8/bin/ruby.exe\"", Gem.ruby
  ensure
    Gem.instance_variable_set("@ruby", orig_ruby)
    Gem::ConfigMap[:bindir] = orig_bindir
    Gem::ConfigMap[:ruby_install_name] = orig_ruby_install_name
    Gem::ConfigMap[:EXEEXT] = orig_exe_ext
  end

  def test_self_ruby_path_without_spaces
    orig_ruby = Gem.ruby
    orig_bindir = Gem::ConfigMap[:bindir]
    orig_ruby_install_name = Gem::ConfigMap[:ruby_install_name]
    orig_exe_ext = Gem::ConfigMap[:EXEEXT]

    Gem::ConfigMap[:bindir] = "C:/Ruby18/bin"
    Gem::ConfigMap[:ruby_install_name] = "ruby"
    Gem::ConfigMap[:EXEEXT] = ".exe"
    Gem.instance_variable_set("@ruby", nil)

    assert_equal "C:/Ruby18/bin/ruby.exe", Gem.ruby
  ensure
    Gem.instance_variable_set("@ruby", orig_ruby)
    Gem::ConfigMap[:bindir] = orig_bindir
    Gem::ConfigMap[:ruby_install_name] = orig_ruby_install_name
    Gem::ConfigMap[:EXEEXT] = orig_exe_ext
  end

  def test_self_ruby_version_1_8_5
    util_set_RUBY_VERSION '1.8.5'

    assert_equal Gem::Version.new('1.8.5'), Gem.ruby_version
  ensure
    util_restore_RUBY_VERSION
  end

  def test_self_ruby_version_1_8_6p287
    util_set_RUBY_VERSION '1.8.6', 287

    assert_equal Gem::Version.new('1.8.6.287'), Gem.ruby_version
  ensure
    util_restore_RUBY_VERSION
  end

  def test_self_ruby_version_1_9_2dev_r23493
    util_set_RUBY_VERSION '1.9.2', -1, 23493

    assert_equal Gem::Version.new('1.9.2.dev.23493'), Gem.ruby_version
  ensure
    util_restore_RUBY_VERSION
  end

  def test_self_searcher
    assert_kind_of Gem::GemPathSearcher, Gem.searcher
  end

  def test_self_set_paths
    other = File.join @tempdir, 'other'
    path = [@userhome, other].join File::PATH_SEPARATOR
    Gem.send :set_paths, path

    assert_equal [@userhome, other, @gemhome], Gem.path
  end

  def test_self_set_paths_nonexistent_home
    ENV['GEM_HOME'] = @gemhome
    Gem.clear_paths

    other = File.join @tempdir, 'other'

    ENV['HOME'] = other

    Gem.send :set_paths, other

    assert_equal [other, @gemhome], Gem.path
  end

  def test_self_source_index
    assert_kind_of Gem::SourceIndex, Gem.source_index
  end

  def test_self_sources
    assert_equal %w[http://gems.example.com/], Gem.sources
  end

  def test_ssl_available_eh
    orig_Gem_ssl_available = Gem.ssl_available?

    Gem.ssl_available = true
    assert_equal true, Gem.ssl_available?

    Gem.ssl_available = false
    assert_equal false, Gem.ssl_available?
  ensure
    Gem.ssl_available = orig_Gem_ssl_available
  end

  def test_self_use_paths
    util_ensure_gem_dirs

    Gem.use_paths @gemhome, @additional

    assert_equal @gemhome, Gem.dir
    assert_equal @additional + [Gem.dir], Gem.path
  end

  def test_self_user_dir
    assert_equal File.join(@userhome, '.gem', Gem.ruby_engine,
                           Gem::ConfigMap[:ruby_version]), Gem.user_dir
  end

  def test_self_user_home
    if ENV['HOME'] then
      assert_equal ENV['HOME'], Gem.user_home
    else
      assert true, 'count this test'
    end
  end

  def test_self_cache_dir
    util_ensure_gem_dirs

    assert_equal File.join(@gemhome, 'cache'), Gem.cache_dir
    assert_equal File.join(@userhome, '.gem', Gem.ruby_engine, Gem::ConfigMap[:ruby_version], 'cache'), Gem.cache_dir(Gem.user_dir)
  end

  def test_self_cache_gem
    util_ensure_gem_dirs

    assert_equal File.join(@gemhome, 'cache', 'test.gem'), Gem.cache_gem('test.gem')
    assert_equal File.join(@userhome, '.gem', Gem.ruby_engine, Gem::ConfigMap[:ruby_version], 'cache', 'test.gem'), Gem.cache_gem('test.gem', Gem.user_dir)
  end

  if Gem.win_platform? then
    def test_self_user_home_userprofile
      skip 'Ruby 1.9 properly handles ~ path expansion' unless '1.9' > RUBY_VERSION

      Gem.clear_paths

      # safe-keep env variables
      orig_home, orig_user_profile = ENV['HOME'], ENV['USERPROFILE']

      # prepare for the test
      ENV.delete('HOME')
      ENV['USERPROFILE'] = "W:\\Users\\RubyUser"

      assert_equal 'W:/Users/RubyUser', Gem.user_home

    ensure
      ENV['HOME'] = orig_home
      ENV['USERPROFILE'] = orig_user_profile
    end

    def test_self_user_home_user_drive_and_path
      skip 'Ruby 1.9 properly handles ~ path expansion' unless '1.9' > RUBY_VERSION

      Gem.clear_paths

      # safe-keep env variables
      orig_home, orig_user_profile = ENV['HOME'], ENV['USERPROFILE']
      orig_home_drive, orig_home_path = ENV['HOMEDRIVE'], ENV['HOMEPATH']

      # prepare the environment
      ENV.delete('HOME')
      ENV.delete('USERPROFILE')
      ENV['HOMEDRIVE'] = 'Z:'
      ENV['HOMEPATH'] = "\\Users\\RubyUser"

      assert_equal 'Z:/Users/RubyUser', Gem.user_home

    ensure
      ENV['HOME'] = orig_home
      ENV['USERPROFILE'] = orig_user_profile
      ENV['HOMEDRIVE'] = orig_home_drive
      ENV['HOMEPATH'] = orig_home_path
    end
  end

  def test_load_plugins
    plugin_path = File.join "lib", "rubygems_plugin.rb"

    Dir.chdir @tempdir do
      FileUtils.mkdir_p 'lib'
      File.open plugin_path, "w" do |fp|
        fp.puts "class TestGem; TEST_SPEC_PLUGIN_LOAD = :loaded; end"
      end

      foo = quick_spec 'foo', '1' do |s|
        s.files << plugin_path
      end

      install_gem foo
    end

    Gem.source_index = nil
    Gem.searcher = nil

    gem 'foo'

    Gem.load_plugins

    assert_equal :loaded, TEST_SPEC_PLUGIN_LOAD
  end

  def test_load_env_plugins
    with_plugin('load') { Gem.load_env_plugins }
    assert_equal :loaded, TEST_PLUGIN_LOAD rescue nil

    util_remove_interrupt_command

    # Should attempt to cause a StandardError
    with_plugin('standarderror') { Gem.load_env_plugins }
    assert_equal :loaded, TEST_PLUGIN_STANDARDERROR rescue nil

    util_remove_interrupt_command

    # Should attempt to cause an Exception
    with_plugin('exception') { Gem.load_env_plugins }
    assert_equal :loaded, TEST_PLUGIN_EXCEPTION rescue nil
  end

  def with_plugin(path)
    test_plugin_path = File.expand_path("test/rubygems/plugin/#{path}",
                                        @@project_dir)

    # A single test plugin should get loaded once only, in order to preserve
    # sane test semantics.
    refute_includes $LOAD_PATH, test_plugin_path
    $LOAD_PATH.unshift test_plugin_path

    capture_io do
      yield
    end
  ensure
    $LOAD_PATH.delete test_plugin_path
  end

  def util_ensure_gem_dirs
    Gem.ensure_gem_subdirectories @gemhome
    @additional.each do |dir|
      Gem.ensure_gem_subdirectories @gemhome
    end
  end

  def util_exec_gem
    spec, _ = quick_spec 'a', '4' do |s|
      s.default_executable = 'exec'
      s.executables = ['exec', 'abin']
    end

    @exec_path = File.join spec.full_gem_path, spec.bindir, 'exec'
    @abin_path = File.join spec.full_gem_path, spec.bindir, 'abin'
  end

  def util_set_RUBY_VERSION(version, patchlevel = nil, revision = nil)
    if Gem.instance_variables.include? :@ruby_version or
       Gem.instance_variables.include? '@ruby_version' then
      Gem.send :remove_instance_variable, :@ruby_version
    end

    @RUBY_VERSION    = RUBY_VERSION
    @RUBY_PATCHLEVEL = RUBY_PATCHLEVEL if defined?(RUBY_PATCHLEVEL)
    @RUBY_REVISION   = RUBY_REVISION   if defined?(RUBY_REVISION)

    Object.send :remove_const, :RUBY_VERSION
    Object.send :remove_const, :RUBY_PATCHLEVEL if defined?(RUBY_PATCHLEVEL)
    Object.send :remove_const, :RUBY_REVISION   if defined?(RUBY_REVISION)

    Object.const_set :RUBY_VERSION,    version
    Object.const_set :RUBY_PATCHLEVEL, patchlevel if patchlevel
    Object.const_set :RUBY_REVISION,   revision   if revision
  end

  def util_restore_RUBY_VERSION
    Object.send :remove_const, :RUBY_VERSION
    Object.send :remove_const, :RUBY_PATCHLEVEL if defined?(RUBY_PATCHLEVEL)
    Object.send :remove_const, :RUBY_REVISION   if defined?(RUBY_REVISION)

    Object.const_set :RUBY_VERSION,    @RUBY_VERSION
    Object.const_set :RUBY_PATCHLEVEL, @RUBY_PATCHLEVEL if
      defined?(@RUBY_PATCHLEVEL)
    Object.const_set :RUBY_REVISION,   @RUBY_REVISION   if
      defined?(@RUBY_REVISION)
  end

  def util_remove_interrupt_command
    Gem::Commands.send :remove_const, :InterruptCommand if
      Gem::Commands.const_defined? :InterruptCommand
  end
end

