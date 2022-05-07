package SuggestsFile;

import Exception.*;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class LinksSuggester {
    private final File file;
    private final List<Suggest> suggests;

    //Метод получения строки для файла config
    public LinksSuggester(File file) throws WrongLinksFormatException, IOException {
        this.file = file;
        suggests = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
            for (String s : lines) {
                String[] line = s.split("\t");
                Suggest suggest = new Suggest(line[0], line[1], line[2]);
                suggests.add(suggest);
            }
        } catch (WrongLinksFormatException msg){
            checkOfValidity();
        }

    }

    //Метод перебора текста и добавления ссылок
    public List<Suggest> suggest(String text) {
        List<Suggest> suggestsText = new ArrayList<>();
        String textLowercase = text.toLowerCase();
        for (Suggest suggest : suggests) {
            int index = textLowercase.indexOf(suggest.getLowercase());
            if (index >= 0) {
                suggestsText.add(suggest);
            }
        }
        return suggestsText;
    }

    //проверка на корректность данных из config
    private boolean checkOfValidity() throws WrongLinksFormatException, IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
        for (String s : lines) {
            String[] recommendations = s.split("\t");
            if (recommendations.length != 3) {
                throw new WrongLinksFormatException("Данные в config неверны.");
            }
        }
        return false;
    }
}
