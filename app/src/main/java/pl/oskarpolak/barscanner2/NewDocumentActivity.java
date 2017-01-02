package pl.oskarpolak.barscanner2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.jb.barcode.BarcodeManager;
import android.jb.utils.Tools;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.oskarpolak.barscanner2.data.Document;
import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

public class NewDocumentActivity extends Activity {


    @BindView(R.id.productsList)
    ListView listView;

    @BindView(R.id.textWIFI)
    TextView textWIFI;



    private  List<Product> products;

    private BarcodeManager manager;
    private BeepManager managerBeep;

    MysqlLocalConnector mysql;

    Document lastFactur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_document);
        ButterKnife.bind(this);

        lastFactur = new Document();


        products = new ArrayList<>();
        listView.setAdapter(new ProductAdapter(products, this));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeProductFromList(products.get(position));
                return false;
            }
        });

        managerBeep = new BeepManager(this, true, true);
        manager = BarcodeManager.getInstance();
        manager.Barcode_Open(this, dataReceived);
        manager.setScanTime(2000);

        mysql = MysqlLocalConnector.getInstance();

        mHandler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Utils.checkWifiConnectionWithMessage(NewDocumentActivity.this);
                textWIFI.setText("Sygnał: " + Utils.getWIFIStrenght(NewDocumentActivity.this) + "/100");
                mHandler.postDelayed(this, 2000);
            }
        };

        chooseType();

        mHandler.post(runnable);
       // tests();
    }

    private void tests() {

       final List<String> eans = new ArrayList<String>();
        eans.add("5904232215896");
//        eans.add("5904232156892");
//        eans.add("5904232176623");
//        eans.add("5904232206023");
//        eans.add("5904232152818");
//        eans.add("5904232166372");
//        eans.add("5904232152863");
//        eans.add("5904232224713");
//        eans.add("5904232158674");
//        eans.add("5904232152849");
//        eans.add("5904232169298");
//        eans.add("5904232220869");
//        eans.add("5904232232848");
//        eans.add("5904232188886");
//        eans.add("5904232227288");
//        eans.add("5904232224157");
//        eans.add("5904232225582");
//        eans.add("5904232223129");
//        eans.add("5904232156366");
//        eans.add("5904232234767");
//        eans.add("5905782086202");
//        eans.add("5904232157219");
//        eans.add("5904232188559");
//        eans.add("5904232234774");

       final Random r1 = new Random();

            Runnable r = new Runnable() {
            @Override
            public void run() {
                result(eans.get(r1.nextInt(eans.size())));

            }
        };

        mHandler.post(r);
    }

    private boolean isParagon = false;

     Handler mHandler;
    private void createDocument() {


        String SQLastFactur = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'FV%' ORDER BY id DESC LIMIT 1";

        String SQLLastParagon = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'PAR%' ORDER BY id DESC LIMIT 1";



        if(!isParagon) {
            new AsyncGetLastFactur(1).execute(SQLastFactur, "1");
        }else{
            new AsyncGetLastFactur(1).execute(SQLLastParagon, "1");
        }

      //  new AsyncGetLastFactur(3).execute(SQLLastParagon, "3");


    }

    private void insertData() {
        String sql;
        if(isParagon) {
           sql = "INSERT INTO `DokHandlowe` (`ID`, `Guid`, `Exported`, `Kategoria`, `Stan`, `Potwierdzenie`, `Definicja`, `Magazyn`, `TypPartii`, `KierunekMagazynu`, `SposobRozliczaniaNadrzednego`, `NumerSymbol`, `NumerNumer`, `NumerPelny`, `Seria`, `Data`, `Czas`, `DataOperacji`, `Kontrahent`, `Odbiorca`, `Osoba`, `SymbolKasy`, `ObcyDataOtrzymania`, `ObcyNumer`, `DostawaTermin`, `DostawaSposob`, `DostawaOdpowiedzialny`, `OsobaKontrahenta`, `LiczonaOd`, `Rabat`, `DefinicjaEwidencji`, `KorektaVAT`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `DataKursu`, `TabelaKursowa`, `KursWaluty`, `BruttoCyValue`, `BruttoCySymbol`, `Poprawiajacy`, `Pracownik`, `MiejsceSwiadczenia`, `WarunkiDostawy`, `RodzajTransportu`, `RodzajTransakcji`, `OkresIntrastat`, `Opis`, `MaxIdent`, `Korekta`, `SposobPrzenoszeniaZaliczki`, `Stamp`, `NumerPelnyZapisany`, `SeriaZapisana`, `CzestotliwoscRozliczania`, `TerminRozliczenia`, `TypTerminuRozliczenia`, `Flags`, `RozliczanieZbiorcze`, `CenaNaPodrzedny`, `RodzajPlatnosciKaucji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `OdbiorcaMiejsceDostawy`, `PrecyzjaCenyWymuszaj`, `PrecyzjaCenyPrecyzja`, `TerminRozliczeniaKaucji`, `InwentaryzacjaInfoBlokowanieTowarow`, `Podpisany`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `DostawaCyklKrotnosc`, `DostawaCyklInterwal`, `DostawaCyklTyp`, `DostawaCyklCzas`, `DostawaCyklPozycjaDnia`, `DostawaCyklRodzajTerminu`, `DostawaCyklSposobNaDniWolne`, `DostawaCyklOkresCyklu`, `DostawaCyklTermin`, `DostawaCyklTermin2`, `DostawaCyklTermin3`, `DostawaCyklTermin4`, `DostawaCyklTermin5`, `DostawaCyklPozycjaDniaZaawansowana`, `EDokumentStatus`, `BuforOpisuAnalitycznego`, `OkresFrom`, `OkresTo`, `EDokumentRodzaj`, `EDokumentStatusData`, `FiltrZasobow`, `KrajPodatkuVat`, `PodzialKosztuDodatkowego`, `CechaPodzialuKosztuDodatkowego`, `RealizacjaStan`, `RealizacjaWynik`) VALUES (NULL, '" + lastFactur.getLastGUID() + "', '0', '2', '0', '0', '6', '1', '0', '-1', '0', 'PAR/*/" + Utils.getLastTwoDigistsOfYear() + "', '"+ lastFactur.getLastFactureId() + "', 'PAR/"+ lastFactur.getFULLIDFVConformed() + "/"+Utils.getLastTwoDigistsOfYear() + "', '', '" + Utils.getData() + "', '907', '" + Utils.getData() + "', '1', '1', '', NULL, '" + Utils.getData() + "', '', '" + Utils.getData() + "', '', '', NULL, '2', '0.000000', NULL, '0', '0.0', '0.0', '0.0', '" + Utils.getData() + "', '1', '1', '0.0', 'PLN', NULL, NULL, 'PL', '0', '0', '0', '" + Utils.getData() + "', '', '4', '0', '0', '" + Utils.getData() + "', 'PAR/" + lastFactur.getFULLIDFVConformed() + "/"+ Utils.getLastTwoDigistsOfYear() + "', '', '0', '1900-01-01 00:00:00', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1900-01-01 00:00:00', '0', NULL, NULL, '0', '2', '1900-01-01 00:00:00', '0', '0', '00000000-0000-0000-0000-000000000000', '0', '0', '', '0.00', 'PLN', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '1900-01-01 00:00:00', '', '176', '0', NULL, '0', '0')";
         }else {
            sql = "INSERT INTO `DokHandlowe` (`ID`, `Guid`, `Exported`, `Kategoria`, `Stan`, `Potwierdzenie`, `Definicja`, `Magazyn`, `TypPartii`, `KierunekMagazynu`, `FiltrZasobow`, `SposobRozliczaniaNadrzednego`, `NumerSymbol`, `NumerNumer`, `NumerPelny`, `NumerPelnyZapisany`, `Seria`, `SeriaZapisana`, `Data`, `Czas`, `DataOperacji`, `CzestotliwoscRozliczania`, `TerminRozliczenia`, `TypTerminuRozliczenia`, `RozliczanieZbiorcze`, `CenaNaPodrzedny`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `Kontrahent`, `Odbiorca`, `OdbiorcaMiejsceDostawy`, `Osoba`, `SymbolKasy`, `ObcyDataOtrzymania`, `ObcyNumer`, `DostawaTermin`, `DostawaSposob`, `DostawaOdpowiedzialny`, `DostawaCyklKrotnosc`, `DostawaCyklInterwal`, `DostawaCyklTyp`, `DostawaCyklCzas`, `DostawaCyklPozycjaDnia`, `DostawaCyklRodzajTerminu`, `DostawaCyklSposobNaDniWolne`, `DostawaCyklOkresCyklu`, `DostawaCyklTermin`, `DostawaCyklTermin2`, `DostawaCyklTermin3`, `DostawaCyklTermin4`, `DostawaCyklTermin5`, `DostawaCyklPozycjaDniaZaawansowana`, `OsobaKontrahenta`, `LiczonaOd`, `Rabat`, `PrecyzjaCenyPrecyzja`, `PrecyzjaCenyWymuszaj`, `DefinicjaEwidencji`, `KorektaVAT`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `KrajPodatkuVat`, `DataKursu`, `TabelaKursowa`, `KursWaluty`, `BruttoCyValue`, `BruttoCySymbol`, `Poprawiajacy`, `Pracownik`, `MiejsceSwiadczenia`, `WarunkiDostawy`, `RodzajTransportu`, `RodzajTransakcji`, `OkresIntrastat`, `Opis`, `MaxIdent`, `Korekta`, `SposobPrzenoszeniaZaliczki`, `RodzajPlatnosciKaucji`, `TerminRozliczeniaKaucji`, `InwentaryzacjaInfoBlokowanieTowarow`, `Flags`, `Podpisany`, `EDokumentStatus`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `BuforOpisuAnalitycznego`, `OkresFrom`, `OkresTo`, `EDokumentRodzaj`, `EDokumentStatusData`, `PodzialKosztuDodatkowego`, `CechaPodzialuKosztuDodatkowego`, `RealizacjaStan`, `RealizacjaWynik`, `Stamp`) VALUES (NULL, '" + lastFactur.getLastGUID() + "', '0', '2', '0', '0', '1', '1', '0', '-1', '', '0', 'FV/*/" + Utils.getLastTwoDigistsOfYear() + "', '" + lastFactur.getLastFactureId() + "', 'FV/" + lastFactur.getFULL() + "/" + Utils.getLastTwoDigistsOfYear() + "', '', '', '', '" + Utils.getDataShort() + "', '928', '" + Utils.getDataShort() + "', '0', '1900-01-01 00:00:00', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1900-01-01 00:00:00', '0', NULL, NULL, NULL, NULL, '', '', '" + Utils.getDataShort() + "', '', '" + Utils.getDataShort() + "', '', '', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', NULL, '1', '0.000000', '1', '0', '71', '0', '0.00', '0.00', '0.00', '176', '" + Utils.getDataShort() + "', '1', '1', '0.00', 'PLN', NULL, NULL, 'PL', '0', '0', '0', '" + Utils.getDataShort() + "', '', '" + products.size() + "', '0', '0', '0', '1900-01-01 00:00:00', '0', '0', '0', '0', '00000000-0000-0000-0000-000000000000', '0', '0', '', '0.00', 'PLN', '1', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '1900-01-01 00:00:00', '0', NULL, '0', '0', '" + Utils.getData() + "')";

        }
        dialog.setMessage("Wstrzykuje dokument");
        new Step1().execute(sql);
    }

    private void step3() {
        dialog.setMessage("Rozpoczynam wstrzykiwanie pozycji");
        new dodajPozycje().execute();
    }

    private void confirmCreateDocument() {
        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno chcesz zatwierdzić?")
                .setMessage("Po zatwierdzeniu dokumentu nie będzie już odwrotu!")
                .setPositiveButton("Tak!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkStany();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("NIE!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void chooseType() {
        new AlertDialog.Builder(this)
                .setTitle("Jaki rodzaj dokumemntu?")
                .setMessage("Wybierz rodzaj dokumentu")
                .setPositiveButton("Faktutra", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isParagon = false;

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Paragon", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isParagon = true;

                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    ProgressDialog dialog;
    private void checkStany() {
         dialog = new ProgressDialog(this);
         dialog.setTitle("Dodaję dokument");
         dialog.setMessage("Rozpoczynam sprawdzanie stanów");

         new sprawdzStany().execute();

         dialog.setIndeterminate(true);
         dialog.show();
    }

    boolean sprawdzownoStan = false;

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(products.size() - 1);
            }
        });

    }


    private boolean checkStanyCount() {
        for (Product p : products) {
            if (p.getCount() == 0) {
                Utils.createDialog(this, "Błąd", "Ilość produktu nie może wynosić 0");
                return false;
            }
            if (p.getCount() < 0) {
                Utils.createDialog(this, "Błąd", "Nie może być ujemnej wartości zasobu!");
                return false;
            }
            if (p.getCount() > p.getStanMag()) {
                Utils.createDialog(this, "Błąd", "Niewystarczająca ilość zasobów w magazynie " + p.getName());
                return false;
            }


        }

        if (products.size() == 0 || products.isEmpty()) {
            Utils.createDialog(this, "Błąd", "Musi istnieć jakiś produkt");
            return false;
        }
        return true;
    }







    @OnClick(R.id.buttonComfirm)
    public void buttonConfirm(){

        Utils.checkWifiConnectionWithMessage(this);


                confirmCreateDocument();


    }

    @OnClick(R.id.buttonInfo)
    public void buttonInfo() {
        Utils.checkWifiConnectionWithMessage(this);
        Utils.showCustomInfoDialog(this, products);
    }

    @OnClick(R.id.buttonAdd)
    public void buttonAddClick(){
        Utils.checkWifiConnectionWithMessage(this);
        // TODO TEST
        new  MakeLoad().execute(manager);
       // tests();
    }


    public  void addProductToList(Product p) {
        products.add(p);
        listView.deferNotifyDataSetChanged();
        ProductAdapter adapter = (ProductAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
    }

    private void removeProductFromList(final Product p){
        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno?")
                .setMessage("Czy na pewno chcesz usunąć " + p.getName() + " z listy?")
                .setPositiveButton("Tak!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        products.remove(p);
                        ProductAdapter adapter = (ProductAdapter) listView.getAdapter();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("NIE!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void addToListOrMenu() {

        for(Product p : listOfProduct) {
            Log.e("debug", "Produkt 1");
        }
        if(listOfProduct.size() > 1) {
            Utils.showCustomInfoDialogPartie(this,listOfProduct );
        }else {
          addProductToList(listOfProduct.get(0));

        }
    }

    // BAZA



    private void protectExit(){
        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno chcesz wyjść?")
                .setMessage("Czy na pewno chcesz wyjść? Utracisz dane faktury!")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(NewDocumentActivity.this, MainActivity.class);
                        finish();
                        startActivity(i);
                    }
                })
                .setNegativeButton("NIE!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onBackPressed() {
        protectExit();
    }

    // Tutaj musimy wdrożyć pobieranie partii
    public List<Product> listOfProduct = new ArrayList<Product>();
    public boolean hasMoreThanOne = false;
    HashMap<String, String> doce = new HashMap<String, String>();
    private class AsyncGetProductByCode extends AsyncTask<String ,Void,Boolean> {


        @Override
        protected void onPreExecute() {
           dialog = new ProgressDialog(NewDocumentActivity.this);
            dialog.setTitle("Nowy produkt");
            dialog.setMessage("Pobieram dane o nowym produkcie..");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();

        }





        @Override
        protected Boolean doInBackground(String... params) {
            listOfProduct.clear();
            doce.clear();
            Statement statement = null;
            Statement statement1 = null;
            Statement statement2 = null;
            Statement statement3 = null;
            hasMoreThanOne = false;
            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();
                statement2 = mysql.getConnection().createStatement();
                statement3 = mysql.getConnection().createStatement();
                ResultSet rs = statement.executeQuery(params[0]);
                Product product;
                boolean pusto = true;
                while (rs.next()) {
                    pusto = false;
                    product = new Product(rs.getString("Nazwa"), rs.getString("id"));

                    // TUTAJ ZAPISANE SĄ ZASOBY
                    ResultSet rs1 = statement1.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + product.getId());

                    List<String> listDocHan = new ArrayList<>();
                    while (rs1.next()) {
                        listDocHan.add(rs1.getString("PartiaDokument"));
                        Log.e("Debug", "Znalazłem zasób");
                    }


                    // Teraz muszę wbić do pozycjiDokHandlowe
                    List<String> listOfPozycjeID = new ArrayList<String>();
                    ResultSet rs3;
                    for (String s : listDocHan) {
                        rs3 = statement3.executeQuery("SELECT * FROM PozycjeDokHan WHERE Dokument = '" + s + "' AND Towar = " + product.getId() + ";");
                        while (rs3.next()) {
                            listOfPozycjeID.add(rs3.getString("ID"));
                            Log.e("Debug", "Znalazłem dodanie w PozycjachDokHan");
                        }
                    }
                    // Teraz odczytuje partie

                    ResultSet rs2;



                    boolean jestPartia = false;
                    for (String s : listOfPozycjeID) {
                        rs2 = statement2.executeQuery("SELECT * FROM Features WHERE Parent = '" + s + "' AND Name = 'Nr_partii'");
                        while (rs2.next()) {
                            Product product1 = new Product(product.getName(), product.getId());
                            product1.setPartion(rs2.getString("Data"));
                            product1.setDoc(s);
                            product1.setHasPartion(true);

                            // tutaj dodaje doce, trzeba uwzględnić dostawy
                            if(doce.containsKey(product1.getPartion())){
                                doce.put(product1.getPartion(), doce.get(product1.getPartion()) + "," + s);
                            }else{
                                doce.put(product1.getPartion(), s);
                            }

                            Log.e("Debug", "Znalazłem partię " + rs2.getString("Data") + " a do niej doc: " + s);
                            addToListOfProducts(product1, true);
                            jestPartia = true;
                        }

                    }
                    if (!jestPartia) {
                        Product product1 = new Product(product.getName(), product.getId());
                        addToListOfProducts(product1, false);
                        product1.setHasPartion(false);

                    }

                    Log.e("debug", "obrót");

                }

                if (pusto) {
                    return true;
                } else {
                    return false;
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }



        @Override
        protected void onPostExecute(Boolean  aVoid) {
            dialog.dismiss();
            if(!aVoid) {
                addToListOrMenu();
            }else{
                Utils.createDialog(NewDocumentActivity.this, "Błąd", "Nie ma takiego kodu kreskowego");
            }
        }
    }

private void addToListOfProducts(Product p, boolean havePartion) {
   if(havePartion) {
       for (Product p1 : listOfProduct) {
           if (p1.getPartion().equals(p.getPartion())) {
               return;
           }
       }
   }
    listOfProduct.add(p);
}



    private class dodajPozycje extends AsyncTask<String ,String,Boolean>{

        @Override
        protected Boolean doInBackground(String ... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();

                int lp = 1;
                String sqlProdukt;

                for (Product p : products) {
                    Log.e("debug", lastFactur.getFacturID());
                    Log.e("debugKRZAK", p.getId() + " " + p.getName());
                    String nazwa;
                    if(p.getName().length() >= 39) {
                        nazwa = p.getName().substring(0, 39);
                        nazwa += ".";
                    }else {
                        nazwa = p.getName();
                    }


                    if(isParagon){
                        sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "', NULL, '-1', '" + lp + "', '" + p.getId() + "', '" + Utils.getDataShort() + "', '1012', '" + nazwa + "', '0', NULL, '0', '10', NULL, '0', '0.230000', '0.230000', '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '61123110', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";

                    }else {
                        sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "', NULL, '-1', '" + (lp)  + "', '" + p.getId() + "', '" + Utils.getDataShort() + "', '1012', '" + nazwa + "', '0', NULL, '0', '10', NULL, '0', '0.230000', '0.230000', '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '61123110', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";
                    }
                    lp++;

                    statement.execute(sqlProdukt);
                    publishProgress("Dodano pozycje " + lp + "/" + products.size());

                }


                return  null;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            Intent i = new Intent(NewDocumentActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            dialog.setMessage("Gotowe!");
            dialog.dismiss();
            Toast.makeText(NewDocumentActivity.this, "GOTOWE", Toast.LENGTH_LONG).show();        }
    }




    private class Step1 extends AsyncTask<String ,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String ... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return  statement.execute(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            String sql;

            dialog.setMessage("Wstrzyknięto!");
            if(!isParagon) {
                sql =   "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'FV%' ORDER BY id DESC LIMIT 1";

            }else {
                sql = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'PAR%' ORDER BY id DESC LIMIT 1";
            }
            dialog.setMessage("Pobieram ostatni znany dokument...");
            new Step2().execute(sql);
        }
    }

    private class Step2 extends AsyncTask<String ,Void, ResultSet>{

        @Override
        protected ResultSet doInBackground(String ... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return  statement.executeQuery(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultSet aVoid) {


                try {
                    while(aVoid.next()) {
                        lastFactur.setFacturID(aVoid.getString("ID"));
                        Log.e("nowe ID", lastFactur.getFacturID() + " to nowe id");
                        dialog.setMessage("Pobrano!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            step3();
         }
    }






    private class AsyncInsertInto extends AsyncTask<String ,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String ... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return  statement.execute(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
          //  Toast.makeText(NewDocumentActivity.this, "Wbiłem inserta! " + aVoid, Toast.LENGTH_SHORT).show();
        }
    }


    private class AsyncGetLastFactur extends AsyncTask<String ,String ,ResultSet>{

        private int id;
        public AsyncGetLastFactur(int id){
              this.id = id;
            }
        @Override
        protected ResultSet doInBackground(String ... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();

                ResultSet rs =  statement.executeQuery(params[0]);
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultSet aVoid) {
            try {
                while(aVoid.next()){
                    if(id == 1) {
                        lastFactur.setLastFactureId(aVoid.getString("NumerNumer"));
                        lastFactur.setLastGUID(aVoid.getString("Guid"));
                        lastFactur.setLastNumerPelnyFV(aVoid.getString("NumerPelny"));
                        lastFactur.setFacturID(aVoid.getString("ID"));
                        dialog.setMessage("Znaleziono!");
                        insertData();
                    }else if(id == 2) {
                        lastFactur.setLastWZId(aVoid.getString("NumerNumer"));
                    }else if(id == 3) {
                        lastFactur.setLastNumerREZFV(aVoid.getString("NumerNumer"));
                        lastFactur.setLastNumerPelnyREZFV(aVoid.getString("NumerPelny"));

                    }
                }
                Log.e("debug", lastFactur.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(String ... values) {
            super.onProgressUpdate(values);
        }
    }




    // SKANER
    public void result(String content) {
        manager.Barcode_Stop();
        managerBeep.play(); //TODO wlaczyc
        String sql = "SELECT * FROM Towary WHERE EAN="+content+";";
        new AsyncGetProductByCode().execute(sql);
    }
    BarcodeManager.Callback dataReceived = new BarcodeManager.Callback() {

        @Override
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
            String codeType = Tools.returnType(buffer);
            String val = null;
            if (codeType.equals("default")) {
                val = new String(buffer);
            } else {
                try {
                    val = new String(buffer, codeType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("val:" + val);
            result(val);
        }
    };


    private class MakeLoad extends AsyncTask<BarcodeManager, Void, Void> {
        @Override
        protected Void doInBackground(BarcodeManager ... params) {


            params[0].Barcode_Start();

            return null;
        }
    }




    private class sprawdzStany extends AsyncTask<String ,Integer, Integer> {

        @Override
        protected void onProgressUpdate(Integer ... values) {
            dialog.setMessage("Sprawdzam zasób " + values[0] + " / " + products.size());
        }

        @Override
        protected Integer doInBackground(String ... params) {
            Statement statement = null;
            Statement statement1 = null;
            int i = 0;
            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();


                int counter1 = 1;
                for (Product p : products) {
                    if (p.isHasPartion()) {

                        // terz musze puscic petle bo tych docow jest wiecej niz 1
                        Log.e("debug", "ma partie");
                        List<String> doce = new ArrayList<String>();
                        for (String s : p.getDoce()) {
                            ResultSet rs1 = statement1.executeQuery("SELECT * FROM PozycjeDokHan WHERE ID=" + s);

                            while (rs1.next()) {
                                doce.add(rs1.getString("Dokument"));
                            }
                        }
                        int counter = 1;
                        for (String s : doce) {
                            String sql = "SELECT * FROM Zasoby WHERE Towar=" + p.getId() + " AND " +
                                    "PartiaDokument=" + s + ";";


                            ResultSet rs = statement.executeQuery(sql);
                            try {
                                while (rs.next()) {
                                    i += Long.parseLong(rs.getString("IloscValue"));
                                    p.addDostawa("Dostawa " + counter + ": " + rs.getString("IloscValue"));
                                    Log.e("debug", "" + rs.getString("IloscValue"));
                                    counter++;
                                }


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e("debug", "nie ma");
                        String sql = "SELECT * FROM Zasoby WHERE Towar=" + p.getId() + " LIMIT 1";

                        try {
                            ResultSet rs = statement.executeQuery(sql);
                            while (rs.next()) {
                                i += Long.parseLong(rs.getString("IloscValue"));
                                Log.e("debug", "" + i);
                            }
                            // nie ma partii
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    counter1++;
                    p.setStanMag(i);
                    publishProgress(counter1);
                }
                }catch(SQLException e){
                    e.printStackTrace();
                }

            return i;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            dialog.setMessage("Sprawdzanie stanów zakończone");
            if(checkStanyCount()){
                createDocument();
            }else{
                dialog.dismiss();
            }


            dialog.setMessage("Pobieram ostatni znany dokument");




        }
    }
}
