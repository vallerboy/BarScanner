package pl.oskarpolak.barscanner2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import pl.oskarpolak.barscanner2.data.Document;
import pl.oskarpolak.barscanner2.data.Partia;
import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

public class NewDocumentActivity extends Activity {


    @BindView(R.id.productsList)
    ListView listView;

    @BindView(R.id.textWIFI)
    TextView textWIFI;


    private static List<Product> products;

    private BarcodeManager manager;
    private BeepManager managerBeep;

    MysqlLocalConnector mysql;

    Document lastFactur;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_document);
        ButterKnife.bind(this);
         context = this;
        lastFactur = new Document();


        products = new ArrayList<>();
        listView.setAdapter(new ProductAdapter(products, this));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeProductFromList(products.get(position), position);
                return true;
            }
        });

        managerBeep = new BeepManager(this, true, true);
        manager = BarcodeManager.getInstance();
        manager.Barcode_Open(this, dataReceived);
        manager.setScanTime(3000);

        mysql = MysqlLocalConnector.getInstance(this);

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
       // tests("5905782067256");
    }


    public static List<Product> getProducts() {
        return products;
    }

    public static void setProducts(List<Product> products) {
        NewDocumentActivity.products = products;
    }


    private void tests(final String s) {

        final List<String> eans = new ArrayList<String>();


        Log.e("debig", "asdsada");
        eans.add("5904232217340");
        //     eans.add("5907516715105");
        //    eans.add("5905782119009");
        //    eans.add("8414299533880");
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
                result(s);

            }
        };

        mHandler.post(r);
    }

    private boolean isParagon = false;

    Handler mHandler;

    private void createDocument() {


        String SQLastFactur = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'FV%' ORDER BY id DESC LIMIT 1";

        String SQLLastParagon = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'PAR%' ORDER BY id DESC LIMIT 1";


        if (!isParagon) {
            new AsyncGetLastFactur(1).execute(SQLastFactur, "1");
        } else {
            new AsyncGetLastFactur(1).execute(SQLLastParagon, "1");
        }

        //  new AsyncGetLastFactur(3).execute(SQLLastParagon, "3");


    }

    private void insertData() {
        Random r = new Random();
        int rand = r.nextInt(9999);
        String sql;
        Log.e("debug", "Products size: " + products.size());
        if (isParagon) {
            sql = "INSERT INTO `DokHandlowe` (`ID`, `Guid`, `Exported`, `Kategoria`, `Stan`, `Potwierdzenie`, `Definicja`, `Magazyn`, `TypPartii`, `KierunekMagazynu`, `SposobRozliczaniaNadrzednego`, `NumerSymbol`, `NumerNumer`, `NumerPelny`, `Seria`, `Data`, `Czas`, `DataOperacji`, `Kontrahent`, `Odbiorca`, `Osoba`, `SymbolKasy`, `ObcyDataOtrzymania`, `ObcyNumer`, `DostawaTermin`, `DostawaSposob`, `DostawaOdpowiedzialny`, `OsobaKontrahenta`, `LiczonaOd`, `Rabat`, `DefinicjaEwidencji`, `KorektaVAT`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `DataKursu`, `TabelaKursowa`, `KursWaluty`, `BruttoCyValue`, `BruttoCySymbol`, `Poprawiajacy`, `Pracownik`, `MiejsceSwiadczenia`, `WarunkiDostawy`, `RodzajTransportu`, `RodzajTransakcji`, `OkresIntrastat`, `Opis`, `MaxIdent`, `Korekta`, `SposobPrzenoszeniaZaliczki`, `Stamp`, `NumerPelnyZapisany`, `SeriaZapisana`, `CzestotliwoscRozliczania`, `TerminRozliczenia`, `TypTerminuRozliczenia`, `Flags`, `RozliczanieZbiorcze`, `CenaNaPodrzedny`, `RodzajPlatnosciKaucji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `OdbiorcaMiejsceDostawy`, `PrecyzjaCenyWymuszaj`, `PrecyzjaCenyPrecyzja`, `TerminRozliczeniaKaucji`, `InwentaryzacjaInfoBlokowanieTowarow`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `DostawaCyklKrotnosc`, `DostawaCyklInterwal`, `DostawaCyklTyp`, `DostawaCyklCzas`, `DostawaCyklPozycjaDnia`, `DostawaCyklRodzajTerminu`, `DostawaCyklSposobNaDniWolne`, `DostawaCyklOkresCyklu`, `DostawaCyklTermin`, `DostawaCyklTermin2`, `DostawaCyklTermin3`, `DostawaCyklTermin4`, `DostawaCyklTermin5`, `DostawaCyklPozycjaDniaZaawansowana`, `BuforOpisuAnalitycznego`, `OkresFrom`, `OkresTo`, `FiltrZasobow`, `KrajPodatkuVat`, `PodzialKosztuDodatkowego`, `CechaPodzialuKosztuDodatkowego`, `RealizacjaStan`, `RealizacjaWynik`) VALUES (NULL, '" + lastFactur.getLastGUID() + "', '0', '2', '0', '0', '6', '1', '0', '-1', '0', 'PAR/*/" + Utils.getLastTwoDigistsOfYear() + "', '" + lastFactur.getLastFactureId() + "', 'PAR/" + lastFactur.getFULLIDFVConformed() + "/" + Utils.getLastTwoDigistsOfYear() + "', '', '" + Utils.getData() + "', '" + rand + "', '" + Utils.getData() + "', '1', '1', '', NULL, '" + Utils.getData() + "', '', '" + Utils.getData() + "', '', '', NULL, '2', '0.000000', NULL, '0', '0.0', '0.0', '0.0', '" + Utils.getData() + "', '1', '1', '0.0', 'PLN', NULL, NULL, 'PL', '0', '0', '0', '" + Utils.getData() + "', '', '" + products.size() + "', '0', '0', '" + Utils.getData() + "', 'PAR/" + lastFactur.getFULLIDFVConformed() + "/" + Utils.getLastTwoDigistsOfYear() + "', '', '0', '1900-01-01 00:00:00', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1900-01-01 00:00:00', '0', NULL, NULL, '0', '2', '1900-01-01 00:00:00', '0', '00000000-0000-0000-0000-000000000000', '0', '0', '', '0.00', 'PLN', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '', '176', '0', NULL, '0', '0')";
        } else {
            sql = "INSERT INTO `DokHandlowe` (`ID`, `Guid`, `Exported`, `Kategoria`, `Stan`, `Potwierdzenie`, `Definicja`, `Magazyn`, `TypPartii`, `KierunekMagazynu`, `FiltrZasobow`, `SposobRozliczaniaNadrzednego`, `NumerSymbol`, `NumerNumer`, `NumerPelny`, `NumerPelnyZapisany`, `Seria`, `SeriaZapisana`, `Data`, `Czas`, `DataOperacji`, `CzestotliwoscRozliczania`, `TerminRozliczenia`, `TypTerminuRozliczenia`, `RozliczanieZbiorcze`, `CenaNaPodrzedny`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `Kontrahent`, `Odbiorca`, `OdbiorcaMiejsceDostawy`, `Osoba`, `SymbolKasy`, `ObcyDataOtrzymania`, `ObcyNumer`, `DostawaTermin`, `DostawaSposob`, `DostawaOdpowiedzialny`, `DostawaCyklKrotnosc`, `DostawaCyklInterwal`, `DostawaCyklTyp`, `DostawaCyklCzas`, `DostawaCyklPozycjaDnia`, `DostawaCyklRodzajTerminu`, `DostawaCyklSposobNaDniWolne`, `DostawaCyklOkresCyklu`, `DostawaCyklTermin`, `DostawaCyklTermin2`, `DostawaCyklTermin3`, `DostawaCyklTermin4`, `DostawaCyklTermin5`, `DostawaCyklPozycjaDniaZaawansowana`, `OsobaKontrahenta`, `LiczonaOd`, `Rabat`, `PrecyzjaCenyPrecyzja`, `PrecyzjaCenyWymuszaj`, `DefinicjaEwidencji`, `KorektaVAT`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `KrajPodatkuVat`, `DataKursu`, `TabelaKursowa`, `KursWaluty`, `BruttoCyValue`, `BruttoCySymbol`, `Poprawiajacy`, `Pracownik`, `MiejsceSwiadczenia`, `WarunkiDostawy`, `RodzajTransportu`, `RodzajTransakcji`, `OkresIntrastat`, `Opis`, `MaxIdent`, `Korekta`, `SposobPrzenoszeniaZaliczki`, `RodzajPlatnosciKaucji`, `TerminRozliczeniaKaucji`, `InwentaryzacjaInfoBlokowanieTowarow`, `Flags`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `BuforOpisuAnalitycznego`, `OkresFrom`, `OkresTo`, `PodzialKosztuDodatkowego`, `CechaPodzialuKosztuDodatkowego`, `RealizacjaStan`, `RealizacjaWynik`, `Stamp`) VALUES (NULL, '" + lastFactur.getLastGUID() + "', '0', '2', '0', '0', '1', '1', '0', '-1', '', '0', 'FV/*/" + Utils.getLastTwoDigistsOfYear() + "', '" + lastFactur.getLastFactureId() + "', 'FV/" + lastFactur.getFULL() + "/" + Utils.getLastTwoDigistsOfYear() + "', 'FV/" + lastFactur.getFULL() + "/" + Utils.getLastTwoDigistsOfYear() + "', '', '', '" + Utils.getData() + "', '" + rand + "', '" + Utils.getData() + "', '0', '1900-01-01 00:00:00', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1900-01-01 00:00:00', '0', NULL, '" + kontrahent + "',NULL, NULL, '', '', '" + Utils.getDataShort() + "', '', '" + Utils.getDataShort() + "', '', '', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', NULL, '1', '0.000000', '1', '0', '71', '0', '0.00', '0.00', '0.00', '176', '" + Utils.getData() + "', '1', '1', '0.00', 'PLN', NULL, NULL, 'PL', '0', '0', '0', '" + Utils.getData() + "', '', '" + products.size() + "', '0', '0', '0', '1900-01-01 00:00:00', '0', '0', '00000000-0000-0000-0000-000000000000', '0', '0', '', '0.00', 'PLN', '1', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', NULL, '0', '0', '" + Utils.getData() + "')";

        }
        dialog.setMessage("Wstrzykuje dokument");
        new Step1().execute(sql);
    }

    private void step3() {
        dialog.setMessage("Rozpoczynam wstrzykiwanie pozycji");
        new dodajPozycje().execute();
    }

    private void confirmCreateDocument() {

        int calosc = 0;

        for (Product p : products) {
            calosc += p.getCount();
        }

        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno chcesz zatwierdzić?")
                .setMessage("Po zatwierdzeniu dokumentu nie będzie już odwrotu!\nCałościwo ilość sztuk" +
                        ": " + calosc + " a pozycji: " + products.size())
                .setPositiveButton("Tak!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isParagon) {
                            showKontrahent();
                        } else {
                            checkStany();
                        }
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

            int sciagnieta = this.getIloscSciagnieta(p);
            if (p.getStanMag() < sciagnieta) {
                Utils.createDialog(this, "Błąd", "Sciągnąłeś za dużo " + p.getName() + "(" + (sciagnieta - p.getStanMag()) + ")");
                return false;
            }

        }

        if (products.size() == 0 || products.isEmpty()) {
            Utils.createDialog(this, "Błąd", "Musi istnieć jakiś produkt");
            return false;
        }
        new usunWszystkiePozycje().execute();
        return true;
    }


    @OnClick(R.id.buttonComfirm)
    public void buttonConfirm() {

        Utils.checkWifiConnectionWithMessage(this);

        if(!MysqlLocalConnector.getInstance(this).isDatabaseConnected()) {
            MysqlLocalConnector.getInstance(this).runDatabase();
            Utils.createDialog(this, "Baza", "Poczekaj próbuje wznowić połączenie");
            return;
        }

        confirmCreateDocument();


    }

    @OnClick(R.id.buttonInfo)
    public void buttonInfo() {
        Utils.checkWifiConnectionWithMessage(this);
        Utils.showCustomInfoDialog(this, products, listView.getFirstVisiblePosition());
    }

    @OnLongClick(R.id.buttonInfo)
    public boolean buttonInfoLong() {
        int ilosc = 0;
        for (Product p : products) {
            ilosc += p.getCount();
        }
        Utils.createDialog(this, "Zbiorczo", "Zbiorczo mamy tutaj " + ilosc + " torebeczek");
        return true;
    }

    @OnClick(R.id.buttonAdd)
    public void buttonAddClick() {
        Utils.checkWifiConnectionWithMessage(this);
        // TODO TEST
        new MakeLoad().execute(manager);
       // tests("5905782067256");
    }


    public void addProductToList(Product p) {
        new dodajPojPozycje().execute(p);
        products.add(p);
        listView.deferNotifyDataSetChanged();
        ProductAdapter adapter = (ProductAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
    }

    private void removeProductFromList(final Product p, final int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno?")
                .setMessage("Czy na pewno chcesz usunąć " + p.getName() + " z listy?")
                .setPositiveButton("Tak!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new usunPojPozycje(pos).execute(products.get(pos));

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

    public void showKontrahent() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_kontrahent);
        dialog.setTitle("Wpisz kontrahenta");

        // set the custom dialog components - text, image and button

        final EditText editText = (EditText) dialog.findViewById(R.id.editTextKontrahent);

        Button dialogRefresh = (Button) dialog.findViewById(R.id.buttonKontrahent);
        dialogRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString() != "") {
                    kontrahent = editText.getText().toString();
                    new AsyncKontrahent().execute("SELECT * FROM Kontrahenci WHERE Kod=" + kontrahent);

                    dialog.dismiss();

                } else {
                    Toast.makeText(NewDocumentActivity.this, "Kontrahent nie może być pusty", Toast.LENGTH_LONG);
                }
            }
        });


        dialog.show();
    }

    String kontrahent = "";

    // TODO partie
    private void addToListOrMenu(Product p) {
        if (p.isHasPartion()) {
            Utils.showCustomInfoDialogPartie(this, p);
        } else {
            Log.e("dafnia", "nie ma partii");
            Utils.showCustomInfoDialogDostawy(this, p);
        }
    }

    // BAZA


    private void protectExit() {
        new AlertDialog.Builder(this)
                .setTitle("Czy na pewno chcesz wyjść?")
                .setMessage("Czy na pewno chcesz wyjść? Utracisz dane faktury!")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(NewDocumentActivity.this, MainActivity.class);
                        finish();
                        startActivity(i);
                        new usunWszystkiePozycje().execute();
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


    private class testProdukt extends AsyncTask<String, Boolean, Boolean> {

        private List<Product> productClass = new ArrayList<>();

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
            try {

                if(MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection() == null){
                  return true;
                }
                Statement statementProdukt = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();
                Statement statementZasob = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();
                Statement statementPozycje = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();
                Statement statementPartie = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();

                Statement statementZasob1 = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();
                Statement statementPozycje1 = MysqlLocalConnector.getInstance(NewDocumentActivity.this).getConnection().createStatement();



                Product p;
                ResultSet produkt = statementProdukt.executeQuery(params[0]);
                Set<String> partieString = new HashSet<>();
                boolean hasPartion = false;
                boolean isEmpty = true;
                int counter = 0;
                int counterPartii = 0;
                int counterProduktu = 0;
                while (produkt.next()) {
                    Log.e("debug", "obrót");
                    counterProduktu++;
                    p = new Product(produkt.getString("Nazwa"), produkt.getString("ID"), produkt.getString("DefinicjaStawki"));
                    ResultSet zasob = statementZasob.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + produkt.getString("ID") + " AND Okres=1");
                    while (zasob.next()) {
                        Log.e("debug", "zasob");
                        counter++;
                        isEmpty = false;
                        ResultSet pozycje = statementPozycje.executeQuery("SELECT * FROM PozycjeDokHan WHERE Ident = " + zasob.getString("PartiaPozycjaIdent") + " AND  Dokument = " + zasob.getString("PartiaDokument") + " AND Towar = " + p.getId());
                        while (pozycje.next()) {
                            Log.e("debug", "pozycja");
                            ResultSet partie = statementPartie.executeQuery("SELECT * FROM Features WHERE Parent = '" + pozycje.getString("ID") + "' AND Name = 'Nr_partii'");
                            while (partie.next()) {
                                Log.e("debug", "partia");
                                Log.e("debugsystem", "Dodaje nową partię");
                                counterPartii++;
                                hasPartion = true;
                                Partia partia = new Partia(partie.getString("Data"));
                                if (!p.isPartiaAdded(partie.getString("Data"))) {
                                    partia.addPozycja(partie.getString("Parent"));
                                    partia.addDokHandlowy(zasob.getString("PartiaDokument"));
                                    partia.addZasob(zasob.getString("ID"));
                                    partia.setId(produkt.getString("ID"));
                                    p.addPartia(partia);
                                } else {
                                    Partia partiaFromProduct = p.getPariaByName(partie.getString("Data"));
                                    partiaFromProduct.addPozycja(partie.getString("Parent"));
                                    partiaFromProduct.addDokHandlowy(zasob.getString("PartiaDokument"));
                                    partiaFromProduct.addZasob(zasob.getString("ID"));
                                    partiaFromProduct.setId(produkt.getString("ID"));
                                }
                                p.setHasPartion(true);
                                hasPartion = true;
                                Log.e("debug", p.getName() + " ustawiam partie!");
                                p.setHasPartion(true);
                            }

                        }

                    }
                    if(hasPartion) {
                        if (counter > counterPartii) {
                            Log.e("nowyzasob", "Coś nie gra, jest zasób bez partii");
                            // jest produkt jeszcze który nie ma partii
                            ResultSet zasob1 = statementZasob1.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + p.getId() + " AND Okres=1");
                            while (zasob1.next()) {
                                // zabezpiecznie przed dodaniem tego samego, powinno zostac dodane
                                // to co juz nie zostalo dodane w partiach
                                Partia partia = new Partia();
                                boolean canAdd = true;
                                for (Partia partia1 : p.getPartie()) {
                                    for (String zasobString : partia1.getZasoby()) {
                                        if (zasobString.equals(zasob1.getString("ID"))) {
                                            Log.e("nowysystem", "Znalazłem to samo ID, omijam");
                                            canAdd = false;
                                        }
                                    }
                                }


                                ResultSet pozycje1 = statementPozycje1.executeQuery("SELECT * FROM PozycjeDokHan WHERE Dokument = " + zasob1.getString("PartiaDokument") + " AND Towar = " + p.getId());
                                while (pozycje1.next()) {
                                    if (canAdd) {
                                        partia.addPozycja(pozycje1.getString("ID"));
                                    }
                                }
                                if (canAdd) {
                                    partia.addZasob(zasob1.getString("ID"));
                                    partia.addDokHandlowy(zasob1.getString("PartiaDokument"));
                                    partia.setPartia("Brak partii");
                                    partia.setId(zasob1.getString("Towar"));
                                    p.addPartia(partia);
                                }
                            }
                        }
                    }

                    if (!hasPartion) {
                        p.setHasPartion(false);
                        ResultSet zasob1 = statementZasob1.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + p.getId() + " AND Okres=1");
                        while (zasob1.next()) {
                            Partia partia = new Partia();
                            partia.setId(zasob1.getString("Towar"));
                            ResultSet pozycje1 = statementPozycje1.executeQuery("SELECT * FROM PozycjeDokHan WHERE Dokument = " + zasob1.getString("PartiaDokument") + " AND Towar = " + p.getId());
                            while (pozycje1.next()) {
                                partia.addPozycja(pozycje1.getString("ID"));
                            }
                            partia.addZasob(zasob1.getString("ID"));
                            partia.addDokHandlowy(zasob1.getString("PartiaDokument"));
                            p.addPartia(partia);
                        }
                    }
                    for (Partia p1 : p.getPartie()) {
                        Log.e("nowysystem", p1.toString());
                    }

                    // Czasami może zdażyć się tak, że EAN się powtórzyć dla dwóch produktów
                    // produkt ma dwie kartoteki


                    productClass.add(p);
                }

                Product scalony = null;
                if(counterProduktu > 1) {
                    Log.e("scalanie", "scalam");
                    scalony = productClass.get(0);
                    for(int i = 1; i < counterProduktu; i++) {
                        for(Partia partia : productClass.get(i).getPartie()) {
                            scalony.addPartia(partia);
                            scalony.setHasPartion(true);
                        }
                    }

                    productClass.clear();
                    productClass.add(scalony);

                    for(Partia partia : productClass.get(0).getPartie()){
                        Log.e("partie", "ID SCALONE TO " + partia.getId());
                    }
                }

                return isEmpty;


            } catch (SQLException e) {
                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            dialog.dismiss();
            if (!aVoid) {

                    addToListOrMenu(productClass.get(0));

            } else {
                Utils.createDialog(NewDocumentActivity.this, "Błąd", "Nie ma takiego kodu kreskowego, lub jego stan magazynowy jest równy 0");
            }
        }
    }

    private class AsyncGetProductByCode extends AsyncTask<String, Void, Boolean> {


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

            Statement statement = null;
            Statement statement1 = null;
            Statement statement2 = null;
            Statement statement3 = null;
            Statement statement4 = null;
            Statement statement5 = null;
            hasMoreThanOne = false;
            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();
                statement2 = mysql.getConnection().createStatement();
                statement3 = mysql.getConnection().createStatement();

                statement4 = mysql.getConnection().createStatement();
                statement5 = mysql.getConnection().createStatement();

                ResultSet rs = statement.executeQuery(params[0]);
                Product product;
                boolean pusto = true;
                while (rs.next()) {

                    product = new Product(rs.getString("Nazwa"), rs.getString("id"), rs.getString("DefinicjaStawki"));

                    Log.e("stawka", "Definicja stawki to: " + product.getDefStawki());
                    // TUTAJ ZAPISANE SĄ ZASOBY

                    ResultSet rs1 = statement1.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + product.getId() + " AND Okres=1;");
                    List<String> listDocHan = new ArrayList<>();
                    while (rs1.next()) {
                        pusto = false;
                        listDocHan.add(rs1.getString("PartiaDokument"));
                        Log.e("Debug", "Znalazłem zasób " + rs1.getString("ID"));
                    }


                    // Teraz muszę wbić do pozycjiDokHandlowe
                    List<String> listOfPozycjeID = new ArrayList<String>();
                    ResultSet rs3;
                    for (String s : listDocHan) {
                        pusto = false;
                        rs3 = statement3.executeQuery("SELECT * FROM PozycjeDokHan WHERE Dokument = '" + s + "' AND Towar = " + product.getId() + ";");
                        while (rs3.next()) {
                            listOfPozycjeID.add(rs3.getString("ID"));
                            Log.e("pozycja", "nowa pozycja dok: " + rs3.getString("ID"));

                        }
                    }
                    // Teraz odczytuje partie

                    ResultSet rs2;


                    boolean jestPartia = false;
                    for (String s : listOfPozycjeID) {
                        rs2 = statement2.executeQuery("SELECT * FROM Features WHERE Parent = '" + s + "' AND Name = 'Nr_partii'");
                        while (rs2.next()) {
                            Product product1 = new Product(product.getName(), product.getId(), product.getDefStawki());
                            product1.setPartion(rs2.getString("Data"));
                            product1.setHasPartion(true);


                            Log.e("Debug", "Znalazłem partię " + rs2.getString("Data") + " a do niej doc: " + s);
                            addToListOfProducts(product1, true);
                            jestPartia = true;

                            // Dodaje zasób w któym znajduje się dana partia i dostawa.
                            String dokHandlowe = "SELECT * FROM PozycjeDokHan WHERE ID = " + s + " AND Towar = " + product1.getId();
                            ResultSet dokSet = statement4.executeQuery(dokHandlowe);
                            while (dokSet.next()) {
                                Log.e("dok", "Dok: " + dokSet.getString("Dokument"));
                                String zasoby = "SELECT * FROM Zasoby WHERE PartiaDokument = " + dokSet.getString("Dokument") + " AND Towar = " + product1.getId();
                                ResultSet zasobySet = statement5.executeQuery(zasoby);
                                while (zasobySet.next()) {
                                    for (Product p : getListOfProducts()) {
                                        if (p.getPartion().equals(rs2.getString("Data"))) {
                                            p.addZasob(zasobySet.getString("ID"));

                                        }
                                    }
                                }
                            }

                            // tutaj dodaje doce, trzeba uwzględnić dostawy
                            for (Product p : getListOfProducts()) {
                                if (p.getPartion().equals(rs2.getString("Data"))) {
                                    p.addDoc(s);
                                    Log.e("debug", "Dodaje nowego doca do tej samej partii");
                                }
                            }

                        }

                    }
                    if (!jestPartia) {
                        // Jeżeli nie znalazłem pozycji to znaczy, że jest to fejkowe dodanie
                        if (!listOfPozycjeID.isEmpty()) {
                            Product product1 = new Product(product.getName(), product.getId(), product.getDefStawki());

                            for (String i : listOfPozycjeID) {
                                product1.addDoc(i);
                                Log.e("PRODUKT", "Dodaje nowa pozycje do produktu");
                            }
                            addToListOfProducts(product1, false);
                            product1.setHasPartion(false);

                        }
                    }

                    Log.e("debug", "obrót");

                }
                statement.close();
                statement1.close();
                statement2.close();
                statement3.close();

                // Czy EAN istnieje?
                if (pusto) {
                    return true;
                } else {
                    return false;
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean aVoid) {
            dialog.dismiss();
            if (!aVoid) {
                // addToListOrMenu();
            } else {
                Utils.createDialog(NewDocumentActivity.this, "Błąd", "Nie ma takiego kodu kreskowego, lub jego stan magazynowy jest równy 0");
            }
        }
    }

    private void addToListOfProducts(Product p, boolean havePartion) {
        if (havePartion) {
            for (Product p1 : listOfProduct) {
                if (p1.getPartion().equals(p.getPartion())) {
                    return;
                }
            }
        }
        listOfProduct.add(p);
    }

    private List<Product> getListOfProducts() {
        return listOfProduct;
    }

    private class dodajPozycje extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();

                int lp = 1;
                String sqlProdukt;
                Random r = new Random();
                int rand = r.nextInt(9999);
                for (Product p : products) {
                    Log.e("debug", lastFactur.getFacturID());
                 //   Log.e("debugKRZAK", p.getId() + " " + p.getName());
                    String nazwa;
                    if (p.getName().length() >= 39) {
                        nazwa = p.getName().substring(0, 39);
                       // nazwa += ".";
                    } else {
                        nazwa = p.getName();
                    }

                    nazwa = nazwa.replaceAll("[^a-zA-Z1-9]", "");


                    Log.e("ERRRRRORRRR", "ID PRODUKTU TO: " + p.getWybranaPartia().getId());

                    if (isParagon) {
                        if (p.isHasPartion()) {
                            sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "','" + p.getWybranaDostawa() + "', '-1', '" + lp + "', '" + p.getWybranaPartia().getId() + "', '" + Utils.getDataShort() + "', '" + rand + "', '" + nazwa + "', '0', NULL, '0', '" + p.getDefStawki() + "', NULL, '0', NULL, NULL, '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";
                        } else {
                            sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "',"  + p.getWybranaDostawa() + " , '-1', '" + lp + "', '" + p.getWybranaPartia().getId() + "', '" + Utils.getDataShort() + "', '" + rand + "', '" + nazwa + "', '0', NULL, '0', '" + p.getDefStawki() + "', NULL, '0', NULL, NULL, '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";

                        }
                    } else {
                        if (p.isHasPartion()) {
                            sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "', '" + p.getWybranaDostawa() + "', '-1', '" + (lp) + "', '" + p.getWybranaPartia().getId() + "', '" + Utils.getDataShort() + "', '" + rand + "', '" + nazwa + "', '0', NULL, '0', '" + p.getDefStawki() + "', NULL, '0', NULL, NULL, '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";
                        } else {
                            sqlProdukt = "INSERT INTO `PozycjeDokHan` (`ID`, `Dokument`, `Ident`, `Dostawa`, `KierunekMagazynu`, `Lp`, `Towar`, `Data`, `Czas`, `Nazwa`, `JestOpis`, `Opis`, `VATOdMarzy`, `DefinicjaStawki`, `DefinicjaPowstaniaObowiazkuVAT`, `StawkaStatus`, `StawkaProcent`, `StawkaZrodlowa`, `StawkaKraj`, `KrajowaStawkaVAT`, `SWW`, `PodstawaZwolnienia`, `NabywcaPodatnik`, `CenaValue`, `CenaSymbol`, `RabatCenyValue`, `RabatCenySymbol`, `Rabat`, `BezRabatu`, `KorektaCeny`, `KorektaRabatu`, `IloscValue`, `IloscSymbol`, `IloscMagazynuValue`, `IloscMagazynuSymbol`, `IloscRezerwowanaValue`, `IloscRezerwowanaSymbol`, `IloscZasobuValue`, `WartoscCyValue`, `WartoscCySymbol`, `SumaNetto`, `SumaVAT`, `SumaBrutto`, `StatusPozycji`, `UmowaInfoCyklKrotnosc`, `UmowaInfoCyklInterwal`, `UmowaInfoCyklTyp`, `UmowaInfoCyklCzas`, `UmowaInfoCyklPozycjaDnia`, `UmowaInfoCyklRodzajTerminu`, `UmowaInfoCyklSposobNaDniWolne`, `UmowaInfoCyklOkresCyklu`, `UmowaInfoCyklTermin`, `UmowaInfoCyklTermin2`, `UmowaInfoCyklTermin3`, `UmowaInfoCyklTermin4`, `UmowaInfoCyklTermin5`, `UmowaInfoCyklPozycjaDniaZaawansowana`, `UmowaInfoDataOkresuRozliczeniowego`, `UmowaInfoWgZuzycia`, `UmowaInfoDomyslnyPodrzedny`, `KompletacjaInfoWspolczynnikNum`, `KompletacjaInfoWspolczynnikDen`, `KompletacjaInfoDodatkowa`, `KompletacjaInfoProporcjaWartosci`, `KompletacjaInfoWartoscValue`, `KompletacjaInfoWartoscSymbol`, `Krotnosc`, `WOkresie`, `Dzien`, `OkresRozliczonyFrom`, `OkresRozliczonyTo`, `DataPrzecenOkresowych`, `KodCN`, `IloscUzupelniajacaValue`, `IloscUzupelniajacaSymbol`, `MasaNettoValue`, `MasaNettoSymbol`, `MasaBruttoValue`, `MasaBruttoSymbol`, `KrajPochodzenia`, `KrajPrzeznaczenia`, `RodzajTransakcji`, `KosztDodatkowy`, `KosztFakturowy`, `KosztStatystyczny`, `KosztMagazynowy`, `DoliczajKosztDodatkowy`, `NumerArkusza`, `NumerWArkuszu`, `WspolczynnikNum`, `WspolczynnikDen`, `Urzadzenie`, `StanPoczatkowyValue`, `StanPoczatkowySymbol`, `Flags`, `GTIN13`, `SchematOpakowan`, `IloscZrealizowanaValue`, `IloscZrealizowanaSymbol`, `ProdukcjaInfoIdentyfikatorElementuPowiazanego`, `ZaliczkaInfoBruttoCyValue`, `ZaliczkaInfoBruttoCySymbol`, `ZmianaParametrowZasobuInfoZmianaParametrowZasobu`, `ZmianaParametrowZasobuInfoIloscValue`, `ZmianaParametrowZasobuInfoIloscSymbol`, `ZmianaParametrowZasobuInfoWartoscCyValue`, `ZmianaParametrowZasobuInfoWartoscCySymbol`, `ParametryRezerwacjiDataOd`, `ParametryRezerwacjiDataDo`, `ParametryRezerwacjiCzasOd`, `ParametryRezerwacjiCzasDo`, `ParametryRezerwacjiPriorytet`, `Stamp`) VALUES (NULL, '" + lastFactur.getFacturID() + "', '" + (lp) + "', "  + p.getWybranaDostawa() + ", '-1', '" + (lp) + "', '" + p.getWybranaPartia().getId() + "', '" + Utils.getDataShort() + "', '" + rand + "', '" + nazwa + "', '0', NULL, '0', '" + p.getDefStawki() + "', NULL, '0', NULL, NULL, '176', '0', '', '', '0', '0.00', 'PLN', '0', 'PLN', '0.000000', '0', '0', '0', '" + p.getCount() + "', 'szt', '" + p.getCount() + "', 'szt', '0', '', '" + p.getCount() + "', '0.00', 'PLN', '0.00', '0.00', '0.00', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + Utils.getDataShort() + "', '0', NULL, '0', '0', '0', '0', '0.00', 'PLN', '0', '0', '0', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '', '0', '$$$', '0', 'kg', '0', 'kg', '', '', '0', '0', '0.00', '0.00', '0.00', '0', '', '0', '1', '1', NULL, '0', '$$$', '0', '', NULL, '0', 'szt', '00000000-0000-0000-0000-000000000000', '0.00', 'PLN', '0', '0', '', '0.00', 'PLN', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '0', '0', NULL, '" + Utils.getData() + "')";

                        }
                    }
                    lp++;

                    statement.execute(sqlProdukt);
                    publishProgress("Dodano pozycje " + lp + "/" + products.size());

                }


                statement.close();
                return null;
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
            Toast.makeText(NewDocumentActivity.this, "GOTOWE", Toast.LENGTH_LONG).show();
        }
    }


    @OnLongClick(R.id.buttonAdd)
    public boolean onAddLongClick() {
        customEAN();
        return true;
    }

    private void customEAN() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_ean);
        //dialog.setTitle("Dodaj EAN");

        // set the custom dialog components - text, image and button

        final EditText editText = (EditText) dialog.findViewById(R.id.editTextKontrahent1);

        Button dialogRefresh = (Button) dialog.findViewById(R.id.buttonKontrahent1);
        Button dialogAnuluj = (Button) dialog.findViewById(R.id.buttonASD);
        dialogRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result(editText.getText().toString());
            }
        });
        dialogAnuluj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private class Step1 extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();
                return statement.execute(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            String sql;

            dialog.setMessage("Wstrzyknięto!");
            if (!isParagon) {
                sql = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'FV%' ORDER BY id DESC LIMIT 1";

            } else {
                sql = "SELECT * FROM DokHandlowe WHERE NumerSymbol LIKE 'PAR%' ORDER BY id DESC LIMIT 1";
            }
            dialog.setMessage("Pobieram ostatni znany dokument...");
            new Step2().execute(sql);
        }
    }

    private class Step2 extends AsyncTask<String, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return statement.executeQuery(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultSet aVoid) {


            try {
                while (aVoid.next()) {
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


    private class AsyncTest extends AsyncTask<String, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return statement.executeQuery(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }












        @Override
        protected void onPostExecute(ResultSet aVoid) {
            //  Toast.makeText(NewDocumentActivity.this, "Wbiłem inserta! " + aVoid, Toast.LENGTH_SHORT).show();
            try {
                while (aVoid.next()) {
                    String ean = aVoid.getString("EAN");
                    tests(ean);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }



    public void runUpdatePojPozycja(Product p,int old, int newL){
        new updatePojPozycja(old, newL).execute(p);
    }

    public class updatePojPozycja extends  AsyncTask<Product, Void, Void>{

        int old, newL;

        public updatePojPozycja(int old, int newL){
            this.old = old;
            this.newL = newL;
        }
        @Override
        protected void onPreExecute() {
           // dialog.show();
          //  dialog.setMessage("Aktualizuje pozycje");
        }

        @Override
        protected Void doInBackground(Product ... params) {
            try {
                Product p = params[0];
                Statement pozycjaStatement = mysql.getConnection().createStatement();
                Statement zasobStatement = mysql.getConnection().createStatement();
                //TODO to moze nie dzialac
                Log.e("nowysystem", "Stara ilosc: " + p.getRoznica() + " nowa ilosc: " + p.getCount());

                int get = newL - old;
                if(p.isHasPartion()) {
                  //  zasobStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta - " + p.getRoznica() + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob() + " AND partia = '" + p.getWybranaPartia().getPartia()+"'");
                    zasobStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta + " + get + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob()  + " AND partia = '" + p.getWybranaPartia().getPartia()+"'");
                }else{
                    zasobStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta + " + get + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          //  dialog.setMessage("Zaktulizowano pozcyje");
          //  dialog.dismiss();
        }


    }


    private class usunPojPozycje extends  AsyncTask<Product, Void, Void>{

        int pos;

        public usunPojPozycje(int pos){
            this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
          //  dialog.show();
          //  dialog.setMessage("Usuwam pozycje");
        }



        @Override
        protected Void doInBackground(Product ... params) {
            try {
                Product p = params[0];
                Statement pozycjaStatement = mysql.getConnection().createStatement();
                Statement updateStatement = mysql.getConnection().createStatement();

                if(p.isHasPartion()) {
                    updateStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta - " + p.getCount() + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob()  + " AND partia = '" + p.getWybranaPartia().getPartia()+"'");
                }else{
                    updateStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta - " + p.getCount() + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob());
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            products.remove(this.pos);
            ProductAdapter adapter = (ProductAdapter) listView.getAdapter();
            adapter.notifyDataSetChanged();
          //  dialog.setMessage("Usunięto");
         //   dialog.dismiss();
        }
    }


    private class usunWszystkiePozycje extends  AsyncTask<Void, Void, Void>{


        public usunWszystkiePozycje(){

        }

        @Override
        protected void onPreExecute() {
            //dialog.show();
           // dialog.setMessage("Usuwam pozycje");
        }



        @Override
        protected Void doInBackground(Void ... params) {
            try {
                for (Product p : products) {
                    Statement pozycjaStatement = mysql.getConnection().createStatement();
                    Statement updateStatement = mysql.getConnection().createStatement();

                    if (p.isHasPartion()) {
                        updateStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta - " + p.getCount() + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob() + " AND partia = '" + p.getWybranaPartia().getPartia() + "'");
                    } else {
                        updateStatement.execute("UPDATE TowaryCloud SET iloscSciagnieta =  iloscSciagnieta - " + p.getCount() + " WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob());
                    }

                }
                }catch(SQLException e){
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // dialog.setMessage("Usunięto");
           // dialog.dismiss();
        }
    }




    private class dodajPojPozycje extends  AsyncTask<Product, String, Boolean>{

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(NewDocumentActivity.this);
            dialog.setTitle("Bratek");
          //  dialog.show();
            dialog.setMessage("Dodaje nową pozycje");
        }

        @Override
        protected Boolean doInBackground(Product ... params) {


            Statement towarycloud = null;
            Statement towarycloudexec = null;
            Random r = new Random();
            int rand = r.nextInt(9999);

            try {

                towarycloud = mysql.getConnection().createStatement();
                towarycloudexec = mysql.getConnection().createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Product p = params[0];
            ResultSet resultCloud = null;
            if(p.isHasPartion()){
                try {
                    resultCloud = towarycloud.executeQuery("SELECT * FROM TowaryCloud WHERE partia = '" + p.getWybranaPartia().getPartia() + "' AND idTowaru = '" + p.getId() + "' AND dostawa = '" + p.getWybranyZasob() +"'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    resultCloud = towarycloud.executeQuery("SELECT * FROM TowaryCloud WHERE idTowaru = " + p.getId() + " AND dostawa = " + p.getWybranyZasob());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            try {
                int counter = 0;
                while (resultCloud.next()) {
                    counter++;
                }
                if(counter == 0){
                    if(p.isHasPartion()) {
                        towarycloud.execute("INSERT INTO TowaryCloud (idTowaru, partia, dostawa, iloscSciagnieta) VALUES ('" + p.getId() + "', '" + p.getWybranaPartia().getPartia() + "', '" + p.getWybranyZasob() + "', '0');");
                    }else{
                        towarycloud.execute("INSERT INTO TowaryCloud (idTowaru, dostawa, iloscSciagnieta) VALUES ('" + p.getId() + "', '" + p.getWybranyZasob() + "', '0');");
                    }
                }
            }catch(SQLException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
           // dialog.setMessage("Dodano pozcyje!"); dialog.dismiss();
        }
    }



    private class AsyncKontrahent extends AsyncTask<String, Boolean, ResultSet> {

        @Override
        protected ResultSet doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return statement.executeQuery(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultSet aVoid) {
            //  Toast.makeText(NewDocumentActivity.this, "Wbiłem inserta! " + aVoid, Toast.LENGTH_SHORT).show();
            int test = 0;
            try {
                while (aVoid.next()) {

                    test = 1;
                    kontrahent = aVoid.getString("ID");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (test == 1) {
                checkStany();
            } else {
                Utils.createDialog(NewDocumentActivity.this, "Kontrahent", "Nie ma takiego kontrahenta");
            }
        }
    }

    public void test() {
        System.out.println("Witaj świecie!");
    }


    private class AsyncInsertInto extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();


                return statement.execute(params[0]);
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


    private class AsyncGetLastFactur extends AsyncTask<String, String, ResultSet> {

        private int id;

        public AsyncGetLastFactur(int id) {
            this.id = id;
        }

        @Override
        protected ResultSet doInBackground(String... params) {
            Statement statement = null;
            try {
                statement = mysql.getConnection().createStatement();

                ResultSet rs = statement.executeQuery(params[0]);
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResultSet aVoid) {
            try {
                while (aVoid.next()) {
                    if (id == 1) {
                        lastFactur.setLastFactureId(aVoid.getString("NumerNumer"));
                        lastFactur.setLastGUID(aVoid.getString("Guid"));
                        lastFactur.setLastNumerPelnyFV(aVoid.getString("NumerPelny"));
                        lastFactur.setFacturID(aVoid.getString("ID"));
                        dialog.setMessage("Znaleziono!");
                        insertData();
                    } else if (id == 2) {
                        lastFactur.setLastWZId(aVoid.getString("NumerNumer"));
                    } else if (id == 3) {
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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }


    // SKANER
    public void result(String content) {
        manager.Barcode_Stop();
        managerBeep.play(); //TODO wlaczyc
        String sql = "SELECT * FROM Towary WHERE EAN=" + content + ";";
        //new AsyncGetProductByCode().execute(sql);
        new testProdukt().execute(sql);
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
        protected Void doInBackground(BarcodeManager... params) {


            params[0].Barcode_Start();

            return null;
        }
    }





    private class sprawdzStany extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.setMessage("Sprawdzam zasób " + values[0] + " / " + products.size());
        }

        @Override
        protected Integer doInBackground(String... params) {
            Statement statement = null;
            Statement statement1 = null;

            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();

                int i = 0;
                int counter1 = 1;
                for (Product product : products) {
                    i = 0;
                    if (product.isHasPartion()) {
                        Log.e("debug", "ma partie");
                        List<String> doce = new ArrayList<String>();
                        String sql = "SELECT * FROM Zasoby WHERE ID = " + product.getWybranyZasob();
                        ResultSet rs = statement.executeQuery(sql);
                        try {
                            while (rs.next()) {
                                i += Integer.valueOf(rs.getString("IloscValue"));

                            }

                            String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + product.getWybranyZasob();
                            ResultSet rs1 = statement.executeQuery(sql1);

                            while(rs1.next()){
                                i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                                i += product.getCount();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        String sql = "SELECT * FROM Zasoby WHERE ID = " + product.getWybranyZasob();
                        String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + product.getWybranyZasob();
                        ResultSet rs1 = statement.executeQuery(sql1);

                        while(rs1.next()){
                            i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                            i += product.getCount();
                        }

                        try {
                            ResultSet rs = statement.executeQuery(sql);
                            while (rs.next()) {
                                i += Long.parseLong(rs.getString("IloscValue"));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    product.setStanMag(i);
                    Log.e("nowysystem", "USTAWIONE JEST: " + product.getCount() + " a na stanie: " + product.getStanMag());
                    publishProgress(counter1);
                    counter1++;
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            dialog.setMessage("Sprawdzanie stanów zakończone");
            postSprawdzStany();

            dialog.setMessage("Pobieram ostatni znany dokument");


        }
    }



    private void postSprawdzStany() {

        if (checkStanyCount()) {
            createDocument();
        } else {
            dialog.dismiss();
        }

    }

    private int getIloscSciagnieta(Product p1) {
        int ilosc = 0;



        for(Product p : NewDocumentActivity.getProducts()) {
            if(p.getWybranyZasob().equals(p1.getWybranyZasob()) && p.getPartion().equals(p1.getPartion())) {
                ilosc += p.getCount();
            }
        }


        Log.e("blad", "Sciagnieta: " + ilosc);
        return ilosc;
    }
}
