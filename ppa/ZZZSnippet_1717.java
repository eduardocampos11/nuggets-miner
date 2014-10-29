public class NoName {
//Count the number of documents available

 File folder = new File("C:\\Users\\user\\fypworkspace\\TextRenderer");
    File[] listOfFiles = folder.listFiles();
    int numDoc = 0;

    for (int i = 0; i < listOfFiles.length; i++) {
        if ((listOfFiles[i].getName().endsWith(".txt"))) {
            numDoc++;
        }
    }

//Count the number of documents containing a string

System.out.println("Please enter the required word  :");
    Scanner scan2 = new Scanner(System.in);
    String word2 = scan2.nextLine();
    String[] array2 = word2.split(" ");

    for (int b = 0; b < array2.length; b++) {
        int numofDoc = 0;



        for (int i = 0; i < filename; i++) {

            try {

                BufferedReader in = new BufferedReader(new FileReader(
                        "C:\\Users\\user\\fypworkspace\\TextRenderer\\abc"
                                + i + ".txt"));

                int matchedWord = 0;

                Scanner s2 = new Scanner(in);

                {

                    while (s2.hasNext()) {
                        if (s2.next().equals(array2[b]))
                            matchedWord++;
                    }

                }
                if (matchedWord > 0)
                    numofDoc++;

            } catch (IOException e) {
                System.out.println("File not found.");
            }

        }
        System.out.println(array2[b] + " --> This number of files that contain the term  " +  numofDoc);
        double inverseTF = Math.log ( numDoc/ numofDoc );
        System.out.println(array2[b] + " --> This inverse term frequency that contain the term  " +  inverseTF);
    }

}
}

}