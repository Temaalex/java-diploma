package ServicePdf;

import Exception.WrongLinksFormatException;
import SuggestsFile.LinksSuggester;
import SuggestsFile.Suggest;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import lombok.Data;
import lombok.SneakyThrows;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class ServicePdf {
    private final File fileIn;
    private final File fileOut;
    private final LinksSuggester linksSuggester;
    private final List<Suggest> suggestsInDoc;

    public static final String DIR_PDFS = "data/pdfs/";
    public static final String DIR_RESULT = "data/converted/";
    public static final String DIR_CONFIG = "data/config";

    public ServicePdf(File fileIn, LinksSuggester linksSuggester){
        this.fileIn = fileIn;
        this.linksSuggester = linksSuggester;
        this.fileOut = new File(DIR_RESULT + fileIn.getName());
        this.suggestsInDoc = new ArrayList<>();
    }


    //Главный метод
    @SneakyThrows
    public void mainMethod() {
        PdfDocument doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(fileOut));
        var countPageAll = doc.getNumberOfPages();
        var numberPage = 1;
        while(numberPage <= countPageAll){
            var page = doc.getPage(numberPage);
            var text = PdfTextExtractor.getTextFromPage(page);
            List<Suggest> suggestsInPage = linksSuggester.suggest(text);
            suggestsInAll(suggestsInDoc, suggestsInPage);
            if (!suggestsInPage.isEmpty()) {
                var newPage = doc.addNewPage(numberPage+1);
                addNewBlock(newPage, suggestsInPage);
                countPageAll++;
                numberPage++;
            }
            numberPage++;
        }
        closeWork(doc);
    }


    //Перебор главного метода
    @SneakyThrows
    public static void startWork (){
        LinksSuggester linksSuggester = new LinksSuggester(new File(DIR_CONFIG));
        var dir = new File(DIR_PDFS);
        File[] files = dir.listFiles();
        for (var fileIn : files) {
            ServicePdf servicePdf = new ServicePdf(fileIn, linksSuggester);
            servicePdf.mainMethod();
        }
    }
    //Завершение работы
    public static void closeWork(PdfDocument doc){
        doc.close();
    }


        //Впомогательные методы:

        //Медот создания вставляемый на страницу блок
        private void addNewBlock (PdfPage newPage, List < Suggest > suggests){
            var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
            Canvas canvas = new Canvas(newPage, rect);
            Paragraph paragraph = new Paragraph("Suggestions:\n");
            paragraph.setFontSize(25);
            for (Suggest suggest : suggests) {
                Link link = addNewLink(rect, suggest);
                paragraph.add(link.setUnderline());
                paragraph.add("\n");
            }
            canvas.add(paragraph);
        }
        //Метод добавления еще одной ссылки
        private Link addNewLink (Rectangle rect, Suggest suggest){
            PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
            PdfAction action = PdfAction.createURI(suggest.getUrl());
            annotation.setAction(action);
            return new Link(suggest.getTitle(), annotation);
        }
        //Метод с проверкой
        private void suggestsInAll (List<Suggest>suggestsInDoc, List<Suggest>suggestsInPage){
            for (int i = 0; i < suggestsInPage.size(); i++) {
                Suggest suggest = suggestsInPage.get(i);
                if (suggestsInDoc.contains(suggest)) {
                    suggestsInPage.remove(i);
                    i--;
                } else {
                    suggestsInDoc.add(suggest);
                }
            }
        }
    }


