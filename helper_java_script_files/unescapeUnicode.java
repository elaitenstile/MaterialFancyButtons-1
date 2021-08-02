import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class unescapeUnicode {
    public static void main(String[] args)
	{	
		File parentDir = new File(".");
		String[] allFiles = parentDir.list();
		List<String> fileNames = new ArrayList<>();
		for (String allFilesFileName : allFiles) {
			if (!allFilesFileName.contains("capitalizeEnum") && 
            !allFilesFileName.contains("unescapeUnicode")
            && allFilesFileName.contains(".")) {
				String[] splits = allFilesFileName.split("\\.");
				System.out.println(allFilesFileName);
				fileNames.add(splits[0]);
			}
		}
		for (String fileName : fileNames)
			try(BufferedReader bfread = new BufferedReader(new FileReader(
				String.format("%s.java",fileName)));
			BufferedWriter bfwrite = new BufferedWriter(new FileWriter(
				String.format("conv/%s.java",fileName)));){

				String line = bfread.readLine();
				boolean insideEnum = false;
				Pattern pattern = Pattern.compile("\\('.u....'\\)");


				while(line!=null){
					if (insideEnum)
					{
						if (line.strip().length() > 4 && !(line.strip().startsWith("//")))
						{
							try {
								if (pattern.matcher(line).find())
								{
                                    line = removeUnicode(line);
								}
							} catch (Exception e) {
								//TODO: handle exception
							}
						}
						if (line.contains("}")) {
							insideEnum = false;
						}
					}
					if (line.contains("enum Icon implements IIcon"))
						insideEnum = true;
					bfwrite.write(line);
					bfwrite.newLine();
					line = bfread.readLine();
				}
			}
			catch (IOException excpt) {
				excpt.printStackTrace();
			}
			catch (StringIndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}

	}
    public static String removeUnicode(String line) {
		// replace unicode sequence with char casted int value
		// also converts enum field to uppercases
        String[] splits = line.split("\\(");
        String part23 = splits[1];
        String[] splitshalf = part23.split("\\)");
        String part2 = splitshalf[0];
        part2 = part2.replace("'\\u", "");
        part2 = part2.replace("'", "");
        assert(part2.length() == 4);

        part2 = "(char) 0x" + part2;
        splitshalf[0] = part2;
        part23 = String.join(")", splitshalf);
        splits[1] = part23;
        System.out.println(part23);
        splits[0] = splits[0].toUpperCase();
        line = String.join("(", splits);
        return line;
    }
}
