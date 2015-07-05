# Reports information about FileSize and File Locations
require 'gist'
require 'json'
exported_libs = Dir["build/libs/Toast-*.jar"]
@data = { :files => {} }
exported_libs.each do |file_name|
  if file_name =~ /.*Toast-(.*)-raw.jar/
    @data[:files][:raw] = file_name
  elsif file_name =~ /.*Toast-(.*)-sources.jar/
    @data[:files][:sources] = file_name
  elsif file_name =~ /.*Toast-(.*).jar/
    @data[:files][:jar] = file_name
  end
end
@data[:sizes] = @data[:files].map { |type, name| {type => File.size(name)} }.reduce(:merge)
puts "Inspector Complete: #{Gist.gist("#{JSON.generate(@data)}")["html_url"]}"
