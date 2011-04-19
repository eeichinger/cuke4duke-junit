require 'rexml/document'

module Cuke4Duke
#  pom_version = REXML::XPath.first(REXML::Document.new(IO.read(File.dirname(__FILE__) + '/../../pom.xml')), '//xmlns:project/xmlns:version/text()').to_s
  pom_version = "0.4.3"
  if pom_version =~ /(.*)-SNAPSHOT$/
    VERSION = "#{$1}.beta"
  else
    VERSION = pom_version
  end
  JAR_NAME = "cuke4duke-#{pom_version}.jar"
end
