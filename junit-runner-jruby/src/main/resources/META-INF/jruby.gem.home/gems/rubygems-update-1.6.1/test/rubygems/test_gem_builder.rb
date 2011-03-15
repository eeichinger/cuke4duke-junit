require 'rubygems/test_case'
require 'rubygems/builder'

class TestGemBuilder < Gem::TestCase

  def test_build
    builder = Gem::Builder.new quick_spec('a')

    use_ui @ui do
      Dir.chdir @tempdir do
        builder.build
      end
    end

    assert_match %r|Successfully built RubyGem\n  Name: a|, @ui.output
  end

  def test_build_validates
    builder = Gem::Builder.new Gem::Specification.new

    assert_raises Gem::InvalidSpecificationException do
      builder.build
    end
  end

end

