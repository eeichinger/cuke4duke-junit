require 'rubygems/test_case'
require 'rubygems/commands/outdated_command'

class TestGemCommandsOutdatedCommand < Gem::TestCase

  def setup
    super

    @cmd = Gem::Commands::OutdatedCommand.new
  end

  def test_initialize
    assert @cmd.handles?(%W[--platform #{Gem::Platform.local}])
  end

  def test_execute
    quick_gem 'foo', '0.1'
    quick_gem 'foo', '0.2'
    remote_10 = quick_spec 'foo', '1.0'
    remote_20 = quick_spec 'foo', '2.0'

    remote_spec_file = File.join @gemhome, 'specifications', remote_10.spec_name
    remote_spec_file = File.join @gemhome, 'specifications', remote_20.spec_name

    @fetcher = Gem::FakeFetcher.new
    Gem::RemoteFetcher.fetcher = @fetcher

    util_setup_spec_fetcher remote_10, remote_20

    use_ui @ui do @cmd.execute end

    assert_equal "foo (0.2 < 2.0)\n", @ui.output
    assert_equal "", @ui.error
  end

end

