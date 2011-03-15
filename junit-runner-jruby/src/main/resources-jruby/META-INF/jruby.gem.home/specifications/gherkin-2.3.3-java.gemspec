# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{gherkin}
  s.version = "2.3.3"
  s.platform = %q{java}

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Mike Sassak", "Gregory Hnatiuk", "Aslak Helles\303\270y"]
  s.date = %q{2010-12-26}
  s.default_executable = %q{gherkin}
  s.description = %q{A fast Gherkin lexer/parser based on the Ragel State Machine Compiler.}
  s.email = %q{cukes@googlegroups.com}
  s.extra_rdoc_files = ["LICENSE", "README.rdoc", "History.txt"]
  s.files = [".gitattributes", ".gitignore", ".mailmap", ".rspec", ".rvmrc", "Gemfile", "History.txt", "LICENSE", "README.rdoc", "Rakefile", "VERSION", "build_native_gems.sh", "cucumber.yml", "features/escaped_pipes.feature", "features/feature_parser.feature", "features/json_formatter.feature", "features/json_parser.feature", "features/native_lexer.feature", "features/parser_with_native_lexer.feature", "features/pretty_formatter.feature", "features/step_definitions/eyeball_steps.rb", "features/step_definitions/gherkin_steps.rb", "features/step_definitions/json_formatter_steps.rb", "features/step_definitions/json_parser_steps.rb", "features/step_definitions/pretty_formatter_steps.rb", "features/steps_parser.feature", "features/support/env.rb", "gherkin.gemspec", "ikvm/.gitignore", "java/.gitignore", "java/src/main/java/gherkin/lexer/i18n/.gitignore", "java/src/main/resources/gherkin/.gitignore", "lib/.gitignore", "lib/gherkin.rb", "lib/gherkin/c_lexer.rb", "lib/gherkin/formatter/ansi_escapes.rb", "lib/gherkin/formatter/argument.rb", "lib/gherkin/formatter/escaping.rb", "lib/gherkin/formatter/filter_formatter.rb", "lib/gherkin/formatter/hashable.rb", "lib/gherkin/formatter/json_formatter.rb", "lib/gherkin/formatter/line_filter.rb", "lib/gherkin/formatter/model.rb", "lib/gherkin/formatter/pretty_formatter.rb", "lib/gherkin/formatter/regexp_filter.rb", "lib/gherkin/formatter/step_printer.rb", "lib/gherkin/formatter/tag_count_formatter.rb", "lib/gherkin/formatter/tag_filter.rb", "lib/gherkin/i18n.rb", "lib/gherkin/i18n.yml", "lib/gherkin/json_parser.rb", "lib/gherkin/lexer/i18n_lexer.rb", "lib/gherkin/listener/event.rb", "lib/gherkin/listener/formatter_listener.rb", "lib/gherkin/native.rb", "lib/gherkin/native/ikvm.rb", "lib/gherkin/native/java.rb", "lib/gherkin/native/null.rb", "lib/gherkin/parser/meta.txt", "lib/gherkin/parser/parser.rb", "lib/gherkin/parser/root.txt", "lib/gherkin/parser/steps.txt", "lib/gherkin/rb_lexer.rb", "lib/gherkin/rb_lexer/.gitignore", "lib/gherkin/rb_lexer/README.rdoc", "lib/gherkin/rubify.rb", "lib/gherkin/tag_expression.rb", "lib/gherkin/version.rb", "ragel/i18n/.gitignore", "ragel/lexer.c.rl.erb", "ragel/lexer.java.rl.erb", "ragel/lexer.rb.rl.erb", "ragel/lexer_common.rl.erb", "spec/gherkin/c_lexer_spec.rb", "spec/gherkin/fixtures/1.feature", "spec/gherkin/fixtures/comments_in_table.feature", "spec/gherkin/fixtures/complex.feature", "spec/gherkin/fixtures/complex.json", "spec/gherkin/fixtures/complex_for_filtering.feature", "spec/gherkin/fixtures/complex_with_tags.feature", "spec/gherkin/fixtures/dos_line_endings.feature", "spec/gherkin/fixtures/hantu_pisang.feature", "spec/gherkin/fixtures/i18n_fr.feature", "spec/gherkin/fixtures/i18n_no.feature", "spec/gherkin/fixtures/i18n_zh-CN.feature", "spec/gherkin/fixtures/scenario_outline_with_tags.feature", "spec/gherkin/fixtures/scenario_without_steps.feature", "spec/gherkin/fixtures/simple_with_comments.feature", "spec/gherkin/fixtures/simple_with_tags.feature", "spec/gherkin/fixtures/with_bom.feature", "spec/gherkin/formatter/ansi_escapes_spec.rb", "spec/gherkin/formatter/filter_formatter_spec.rb", "spec/gherkin/formatter/model_spec.rb", "spec/gherkin/formatter/pretty_formatter_spec.rb", "spec/gherkin/formatter/spaces.feature", "spec/gherkin/formatter/step_printer_spec.rb", "spec/gherkin/formatter/tabs.feature", "spec/gherkin/formatter/tag_count_formatter_spec.rb", "spec/gherkin/i18n_spec.rb", "spec/gherkin/java_lexer_spec.rb", "spec/gherkin/java_libs.rb", "spec/gherkin/json_parser_spec.rb", "spec/gherkin/lexer/i18n_lexer_spec.rb", "spec/gherkin/output_stream_string_io.rb", "spec/gherkin/parser/parser_spec.rb", "spec/gherkin/rb_lexer_spec.rb", "spec/gherkin/sexp_recorder.rb", "spec/gherkin/shared/lexer_group.rb", "spec/gherkin/shared/py_string_group.rb", "spec/gherkin/shared/row_group.rb", "spec/gherkin/shared/tags_group.rb", "spec/gherkin/tag_expression_spec.rb", "spec/spec_helper.rb", "tasks/bench.rake", "tasks/bench/feature_builder.rb", "tasks/bench/generated/.gitignore", "tasks/bench/null_listener.rb", "tasks/compile.rake", "tasks/cucumber.rake", "tasks/gems.rake", "tasks/ikvm.rake", "tasks/ragel_task.rb", "tasks/rdoc.rake", "tasks/release.rake", "tasks/rspec.rake", "lib/gherkin.jar"]
  s.homepage = %q{http://github.com/aslakhellesoy/gherkin}
  s.rdoc_options = ["--charset=UTF-8"]
  s.require_paths = ["lib"]
  s.rubygems_version = %q{1.3.6}
  s.summary = %q{gherkin-2.3.3}
  s.test_files = ["features/escaped_pipes.feature", "features/feature_parser.feature", "features/json_formatter.feature", "features/json_parser.feature", "features/native_lexer.feature", "features/parser_with_native_lexer.feature", "features/pretty_formatter.feature", "features/step_definitions/eyeball_steps.rb", "features/step_definitions/gherkin_steps.rb", "features/step_definitions/json_formatter_steps.rb", "features/step_definitions/json_parser_steps.rb", "features/step_definitions/pretty_formatter_steps.rb", "features/steps_parser.feature", "features/support/env.rb", "spec/gherkin/c_lexer_spec.rb", "spec/gherkin/fixtures/1.feature", "spec/gherkin/fixtures/comments_in_table.feature", "spec/gherkin/fixtures/complex.feature", "spec/gherkin/fixtures/complex.json", "spec/gherkin/fixtures/complex_for_filtering.feature", "spec/gherkin/fixtures/complex_with_tags.feature", "spec/gherkin/fixtures/dos_line_endings.feature", "spec/gherkin/fixtures/hantu_pisang.feature", "spec/gherkin/fixtures/i18n_fr.feature", "spec/gherkin/fixtures/i18n_no.feature", "spec/gherkin/fixtures/i18n_zh-CN.feature", "spec/gherkin/fixtures/scenario_outline_with_tags.feature", "spec/gherkin/fixtures/scenario_without_steps.feature", "spec/gherkin/fixtures/simple_with_comments.feature", "spec/gherkin/fixtures/simple_with_tags.feature", "spec/gherkin/fixtures/with_bom.feature", "spec/gherkin/formatter/ansi_escapes_spec.rb", "spec/gherkin/formatter/filter_formatter_spec.rb", "spec/gherkin/formatter/model_spec.rb", "spec/gherkin/formatter/pretty_formatter_spec.rb", "spec/gherkin/formatter/spaces.feature", "spec/gherkin/formatter/step_printer_spec.rb", "spec/gherkin/formatter/tabs.feature", "spec/gherkin/formatter/tag_count_formatter_spec.rb", "spec/gherkin/i18n_spec.rb", "spec/gherkin/java_lexer_spec.rb", "spec/gherkin/java_libs.rb", "spec/gherkin/json_parser_spec.rb", "spec/gherkin/lexer/i18n_lexer_spec.rb", "spec/gherkin/output_stream_string_io.rb", "spec/gherkin/parser/parser_spec.rb", "spec/gherkin/rb_lexer_spec.rb", "spec/gherkin/sexp_recorder.rb", "spec/gherkin/shared/lexer_group.rb", "spec/gherkin/shared/py_string_group.rb", "spec/gherkin/shared/row_group.rb", "spec/gherkin/shared/tags_group.rb", "spec/gherkin/tag_expression_spec.rb", "spec/spec_helper.rb"]

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 3

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<json>, ["~> 1.4.6"])
      s.add_development_dependency(%q<rake>, ["~> 0.8.7"])
      s.add_development_dependency(%q<bundler>, ["~> 1.0.7"])
      s.add_development_dependency(%q<rspec>, ["~> 2.3.0"])
      s.add_development_dependency(%q<awesome_print>, ["~> 0.2.1"])
      s.add_development_dependency(%q<cucumber>, ["~> 0.10.0"])
      s.add_development_dependency(%q<term-ansicolor>, ["~> 1.0.5"])
      s.add_development_dependency(%q<builder>, ["~> 3.0.0"])
    else
      s.add_dependency(%q<json>, ["~> 1.4.6"])
      s.add_dependency(%q<rake>, ["~> 0.8.7"])
      s.add_dependency(%q<bundler>, ["~> 1.0.7"])
      s.add_dependency(%q<rspec>, ["~> 2.3.0"])
      s.add_dependency(%q<awesome_print>, ["~> 0.2.1"])
      s.add_dependency(%q<cucumber>, ["~> 0.10.0"])
      s.add_dependency(%q<term-ansicolor>, ["~> 1.0.5"])
      s.add_dependency(%q<builder>, ["~> 3.0.0"])
    end
  else
    s.add_dependency(%q<json>, ["~> 1.4.6"])
    s.add_dependency(%q<rake>, ["~> 0.8.7"])
    s.add_dependency(%q<bundler>, ["~> 1.0.7"])
    s.add_dependency(%q<rspec>, ["~> 2.3.0"])
    s.add_dependency(%q<awesome_print>, ["~> 0.2.1"])
    s.add_dependency(%q<cucumber>, ["~> 0.10.0"])
    s.add_dependency(%q<term-ansicolor>, ["~> 1.0.5"])
    s.add_dependency(%q<builder>, ["~> 3.0.0"])
  end
end
