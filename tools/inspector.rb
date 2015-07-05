# Reports information about FileSize and File Locations
require 'json'
require 'zlib'
require 'base64'
begin
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
    puts "-- METRICS --"
    puts Base64.encode64 Zlib::Deflate.deflate(File.read("tools/stats/metrics.json").split("\n").map {|x| x.gsub(/\s*/, "")}.join())
    puts "-- END METRICS --"
    puts "-- INSPECTOR --"
    puts Base64.encode64 Zlib::Deflate.deflate(JSON.generate(@data))
    puts "-- END INSPECTOR --"
rescue => e
  puts "Could not Upload Metrics :c"
  raise e
end
