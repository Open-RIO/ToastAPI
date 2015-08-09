# Checks code Imports to ensure that they will work with the RoboRIO JDK
require 'open-uri'
require 'openssl'
CLASSLIST = "https://gist.githubusercontent.com/JacisNonsense/910b501ffce221c0a41b/raw"
class_list_content = open(CLASSLIST, {ssl_verify_mode: OpenSSL::SSL::VERIFY_NONE}).read.split("\n")

exported_libs = Dir["build/libs/Toast-*.jar"]
toast_jar = ""
for filename in exported_libs.select {|i| i[/.*Toast-([^-[a-zA-Z]]*).jar/]}
  toast_jar = filename
end

# Toast Jar Classes -- Raw names of each Source file
toast_jar_classes = Dir["src/main/java/**/*.java"]
# Toast Jar Classes -- Remove src/main/java prefix
toast_jar_classes = toast_jar_classes.map {|str| str.gsub(/src.main.java./, "")}
# Toast Jar Classes (Stub) -- Remove extension and format as package name
toast_jar_classes_stub = toast_jar_classes.map {|str| str.gsub(/\//, ".").gsub(/\.(class|java)$/, "")}

# List all the entries in the Compiled Toast Jar to make sure libraries are counted
class_list_content += ` jar tf #{toast_jar} `.split("\n").grep /.*.class/
# Format everything in the ClassList in package form
classlist = class_list_content.map {|str| str.gsub(/\//, ".").gsub(/\.(class|java)$/, "")}

# AWT and Swing packages aren't required, as GUI operation is only done in Sim
# Also ignore Nashorn extensions because of ButterKnife
EXCLUSIONS = ["java.awt.*", "javax.swing.*", "javax.image.*",
  "jdk.nashorn.*", "jdk.internal.*"]

puts "Classes To Check: #{toast_jar_classes.length}"
puts "Valid Robot Classes: #{classlist.length}"

@errored = false

for current_class in toast_jar_classes_stub
  puts "Checking: #{current_class}"
  checklist = `javap -verbose -classpath #{toast_jar} -p #{current_class} `.split("\n")
    .grep(/#[0-9]* = Class.* \/\/ ([^\"].*)/).map { |x| x.gsub(/.*\/\/ (.*)/, "\\1").gsub(/\//, ".") }

  for importcheck in checklist
    importcheck = importcheck.gsub /(\$\S*)*/, ""   # For things like jaci.openrio.toast.core.Environment$OS (nested classes)
    errored = !(classlist.include?(importcheck) || (EXCLUSIONS.select {|x| importcheck =~ /#{x}/}.length != 0))
    @errored = true if errored
    puts "Error: import #{importcheck} on file #{current_class}" if errored
  end
end

if @errored; puts "Error Found: Aborting..."; exit 1; end
