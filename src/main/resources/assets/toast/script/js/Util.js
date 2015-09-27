var $EXEC = function() {
    var builder = new java.lang.ProcessBuilder(arr_to_vector([].slice.call(arguments)));
    builder.redirectErrorStream(true);
    var process = builder.start();
    var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
    var line = "";
    var out = "";
    while ((line = reader.readLine()) != null) {
        out += line + "\n";
    }
    process.waitFor();
    reader.close();
    return out;
};