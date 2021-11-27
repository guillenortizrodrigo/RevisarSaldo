package com.innova.checarsaldoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;


import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.enercon.updateapps.AppUtils;
import com.enercon.updateapps.UpdateChecker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.innova.checarsaldoapp.adaptador.OnRecyclerItemListener;
import com.innova.checarsaldoapp.model.ErrorHandler;
import com.pos.api.Mcr;

import com.innova.checarsaldoapp.model.SquareItem;
import com.innova.checarsaldoapp.adaptador.SquareItemAdapter;

import android.widget.LinearLayout;

import com.innova.checarsaldoapp.model.RestService;

import android.os.Handler;
import android.os.Message;
import android.content.DialogInterface;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.innova.checarsaldoapp.fragmento.FragmentUtils;
import com.pos.api.Scan;
import com.innova.checarsaldoapp.actividad.CaptureActivityPortrait;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnRecyclerItemListener<SquareItem> {

    private List<SquareItem> lstOptions;
    private SquareItemAdapter adapter;
    private SquareItem currentEntity;
    private RecyclerView rvPrintOptions;
    private ViewFlipper simpleViewFlipper;
    private NestedScrollView contenedorDatos;
    private LinearLayout contenedorBotones;
    private ScrollView contenedorOpciones;
    private int codigoHTTP;
    private String respuestaJSON;
    private Context ctx;
    private JSONObject cliente;
    private JSONObject autorizacion;
    private AutoCompleteTextView limite;
    private AutoCompleteTextView monto;
    private AutoCompleteTextView odometrotrx;
    private AutoCompleteTextView pintrx;
    private AutoCompleteTextView posiciontrx;
    private String valortrack1;
    private Scan scan;
    private Timer mScanCodeTimer;
    private TimerTask mScanCodeTask;
    private String IP, licencia;
    private EditText licenciatxt;
    private LinearLayout contenedorMonto;
    private String Terminal;

    private Spinner pos;

    private Mcr mcr;

    RestService restServiceG, restServiceP, restServicePut, restServicePp;

    private TextView textoEstado;
    private ImageView imagenEsatdo;
    private TextView textCount;
    private TextView textSuccess;
    private static int totalCount = 0;
    private static int successCount = 0;
    //check card timer
    private Timer mCheckCardTimer;
    private TimerTask mCheckCardTask;
    private static int CHECK_CONNECT_STATUS_TIMEOUT = 10000;//10s
    private boolean timeout = false;
    private String tipo;
    private boolean isodometro;
    private boolean isnip;
    private boolean islimite;
    private double saldo;
    private Button btnEnviar;
    private Button btnCancela;
    private String mensajeVD;


    private static final int RECORD_PROMPT_MSG = 0x02;
    private static final int TCOUNT_PROMPT_MSG = 0x03;
    private static final int SCOUNT_PROMPT_MSG = 0x04;

    private JSONArray posicionesJSON;
    private JSONObject items;
    private ArrayList<Integer> posicionesdecarga_disponibles = new ArrayList<>();
    private ArrayList<String> comboposiciones_carga = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("versionName = " + AppUtils.getVersionName(this) + " versionCode = " + AppUtils.getVersionCode(this));
        UpdateChecker.checkForDialog(MainActivity.this);
        setContentView(R.layout.activity_main);
        this.ctx = (Context) this;
        cargarPreferencia();

        Terminal = Build.SERIAL.toString();
        //Toast.makeText(ctx, Terminal, Toast.LENGTH_SHORT).show();

        //
        mcr = new Mcr(this, new Mcr.McrConnectStatusListener() {
            @Override
            public void onConnectResult(boolean bRet) {
            }
        });

        scan = new Scan(this, new Scan.ScanConnectStatusListener(){
            @Override
            public void onConnectResult(boolean bRet) {
            }
        });

        this.ctx=this;
        this.rvPrintOptions = (RecyclerView)findViewById(R.id.rvOptions);
        this.adapter = new SquareItemAdapter(this.lstOptions, this);
        this.rvPrintOptions.setAdapter((RecyclerView.Adapter)this.adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager((Context)this, 1, RecyclerView.VERTICAL, false);
        this.rvPrintOptions.setLayoutManager((RecyclerView.LayoutManager)gridLayoutManager);
        this.contenedorDatos=(NestedScrollView)findViewById(R.id.contenedorDatos);
        this.contenedorBotones=(LinearLayout)findViewById(R.id.contenedorBotones);
        this.contenedorOpciones = (ScrollView) findViewById(R.id.contenedorOpciones);
        this.imagenEsatdo=(ImageView) findViewById(R.id.imagenEstado);
        this.textoEstado=(TextView)findViewById(R.id.textoEstado);

        getCardOptions();
        this.currentEntity = this.lstOptions.get(0);
        simpleViewFlipper = (ViewFlipper) findViewById(R.id.simpleViewFlipper); // get the reference of ViewFlipper
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewFlipper
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);
        hideAllItems(true);



    }

    private void getCardOptions() {
        this.lstOptions = new ArrayList<SquareItem>();
        this.lstOptions.add(new SquareItem(R.drawable.ic_card_magnetic, "Banda", "#008577", true));
        this.lstOptions.add(new SquareItem(R.drawable.barcode, "Código", "#008577", false));
        //this.lstOptions.add(new SquareItem(R.drawable.ic_barcode, "Tag", "#008577", false));
        //this.lstOptions.add(new SquareItem(R.drawable.ic_vale, "Vale", "#008577", false));
        this.adapter.setLstProductCategory(this.lstOptions);
        this.adapter.notifyDataSetChanged();
    }

    private void selectItem(SquareItem paramSquareItem) {
        Iterator n = this.lstOptions.iterator();
        while (n.hasNext())
            ((SquareItem)n.next()).setActive(false);
        for (SquareItem squareItem : this.lstOptions) {
            if (squareItem.getTitle().equals(paramSquareItem.getTitle()))
                squareItem.setActive(true);
        }
        this.adapter.notifyDataSetChanged();
    }

    private void ErrorConexionM() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this.ctx);
        builder.setTitle("Error de Conexión");
        builder.setIcon(R.drawable.error);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param2DialogInterface, int param2Int) {
                finish();
            }
        });
        builder.show();
    }

    public void onClickListener(View paramView, SquareItem paramSquareItem) {
        this.currentEntity = paramSquareItem;
        tipo=paramSquareItem.getTitle();
        if (paramSquareItem.getTitle().equals("Banda")) {
            //Toast.makeText(ctx, licencia, Toast.LENGTH_SHORT).show();
            System.err.println("LECTURA DE BANDA");
            tipo="banda";
            //String request="{\"License\":\"YjBhNWU2YWEtYzUzZS00OWU5LWFiYTQtNzI2NDUyOWU4YzI4\",\"TrxUrl\":\"/card/data\"}";
            //String request="{\"License\":\"NWZkNmQ3N2MtMTA5My00NTFiLWFhMjUtMmY2MzFlNWZiMTcx\",\"TrxAmount\":\"45.56\",\"TrxCurrency\":\"1\",\"TrxReference\":\"reference\",\"TrxUrl\":\"https://integration.intelipos.io/card/data\",\"TrxUser\":\"user\"}";
            String request="{\"License\":\""+licencia+"\",\"TrxAmount\":\"45.56\",\"TrxCurrency\":\"1\",\"TrxReference\":\"reference\",\"TrxUrl\":\"https://integration.intelipos.io/card/data\",\"TrxUser\":\"user\"}";
            System.err.println(request);
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse( "https://integration.intelipos.io/card/data".toString()));
            intent.putExtra("request",request );
            startActivityForResult(intent, 322);

        } else if (paramSquareItem.getTitle().equals("Código") || paramSquareItem.getTitle().equals("Vale")) {
            System.err.println("BARRAS");
            tipo=paramSquareItem.getTitle().toString().toLowerCase();
            scanQr();


        } else if (paramSquareItem.getTitle().equals("Tag")) {

            tipo="tag";
            //String request="{\"License\":\"YjBhNWU2YWEtYzUzZS00OWU5LWFiYTQtNzI2NDUyOWU4YzI4\",\"TrxUrl\":\"/card/data\"}";
            String request="{\"License\":\""+licencia+"\",\"TrxAmount\":\"45.56\",\"TrxCurrency\":\"1\",\"TrxReference\":\"reference\",\"TrxUrl\":\"https://integration.intelipos.io/tag/data\",\"TrxUser\":\"user\"}";
            System.err.println(request);
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse( "https://integration.intelipos.io/tag/data".toString()));
            intent.putExtra("request",request );
            startActivityForResult(intent, 322);
            return;

        }
        selectItem(paramSquareItem);
    }

    private void hideAllItems(boolean opc) {
        if(opc){
            this.contenedorDatos.setVisibility(View.GONE);
            this.contenedorBotones.setVisibility(View.GONE);
            this.contenedorOpciones.setVisibility(View.VISIBLE);
        }else{
            this.contenedorDatos.setVisibility(View.VISIBLE);
            this.contenedorBotones.setVisibility(View.VISIBLE);
            this.contenedorOpciones.setVisibility(View.GONE);
        }
    }

    private void ValidaCuenta(String valor){
        try{
            valortrack1=valor;
            String json;
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            String tip = tipo.toLowerCase();
            //Toast.makeText(ctx, tip, Toast.LENGTH_SHORT).show();
            if(tip.equals("código")){
                tip = "codigo";
            }
            //jsonObject.put("tipo",tip);
            jsonObject.put("tipo",tip);
            jsonObject.put("datos", valor);
            jsonObject.put("terminal",Terminal);
            //noinspection AccessStaticViaInstance
            //jsonObject.put("dealcode", CDealCode.getInstance().getS_szDealCode());// dealCode
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            // 5. set json to StringEntity
            System.out.println("InputStream...." + json);
            restServiceG.setEntity(json);
            restServiceG.execute();
        }catch(org.json.JSONException j){
            j.printStackTrace();
        }
    }

    private final Handler mHandlerGet = new Handler(){
        @Override
        public void handleMessage(Message msg){
           // t_query1.setText((String) msg.obj);
            codigoHTTP=msg.arg2;
            //Toast.makeText(ctx, String.valueOf(codigoHTTP), Toast.LENGTH_SHORT).show();
            respuestaJSON=(String) msg.obj;
            System.out.println("Respuesta del WebService codigo:"+msg.arg2+" json: "+(String) msg.obj);
            //simpleViewFlipper.showPrevious();
            try{
                //cliente=new JSONObject(respuestaJSON);
                //hideAllItems(((codigoHTTP==200)?false:true));

                if(codigoHTTP == 0){
                    ErrorConexionM();
                }else if(codigoHTTP!=200){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this.ctx);
                    builder.setTitle("Notificacion");
                    builder.setIcon(R.drawable.error);
                    builder.setCancelable(false);
                    //builder.setMessage(autorizacion.getString("mensaje"));
                    builder.setMessage((respuestaJSON.contains("mensaje"))?new JSONObject(respuestaJSON).getString("mensaje"):respuestaJSON.substring(0,((respuestaJSON.length()<200)?respuestaJSON.length():200)));
                    //builder.setMessage((codigoHTTP!=200)?respuestaJSON:new JSONObject(respuestaJSON).getString("mensaje"));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface param2DialogInterface, int param2Int) {
                            finish();
                        }
                    });
                    builder.show();
                } else if(codigoHTTP == 200){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this.ctx);
                    builder.setTitle("Mensaje de Servidor");
                    builder.setCancelable(false);
                    //builder.setIcon(R.drawable.error);
                    builder.setMessage((respuestaJSON.contains("respuesta"))?new JSONObject(respuestaJSON).getString("respuesta"):respuestaJSON.substring(0,((respuestaJSON.length()<200)?respuestaJSON.length():200)));
                    //builder.setMessage((codigoHTTP!=200)?respuestaJSON:new JSONObject(respuestaJSON).getString("descripcion"));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface param2DialogInterface, int param2Int) {
                            finish();
                        }
                    });
                    builder.show();
                }else{
                    Log.d("JSON RECIBIDO", respuestaJSON);
                }
            }catch(Exception e){

            }
        }
    };








    private void limpiar(){
        monto.setText("");
        odometrotrx.setText("");
        pintrx.setText("");
        limite.setText("");
        //posiciontrx.setText("");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (null != mcr) mcr.DLL_McrRelease();
        if(null!= scan) scan.DLL_ScanRelease();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.opcion_config){
            abrirConfiguracion();

        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarPreferencia(){
        int launch_configuracion = 3;
        Intent i = this.getPackageManager().getLaunchIntentForPackage("com.inservice.fuelsoft_config");
        i.setAction(Intent.ACTION_SEND);
        i.setFlags(0);
        i.putExtra("request","SI");
        startActivityForResult(i,launch_configuracion);

    }

    private void setPreferencias(){
        SharedPreferences prefs =
                getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IP",this.IP);
        editor.putString("licencia",this.licencia);
        editor.commit();

    }

    private void abrirConfiguracion(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Escribe la contraseña");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        input.requestFocus();
        input.setFocusable(true);
        builder.setView(input);


        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if(input.getText().toString().equalsIgnoreCase("1nn0v4@3n3rc0n")){
                if(input.getText().toString().equalsIgnoreCase("#1nn0v4@3n3rc0n#E")){
                    //setConfiguracion2();
                    createLoginDialogo().show();
                }else{
                    Toast.makeText(getApplicationContext(), "Acceso denegado", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    public android.app.AlertDialog createLoginDialogo() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_config, null);
        builder.setView(v);

        final EditText inputIP = (EditText) v.findViewById(R.id.iptxt);
        final EditText licenciatxt = (EditText) v.findViewById(R.id.txtlicencia);
        inputIP.setInputType(InputType.TYPE_CLASS_TEXT );
        inputIP.setText(IP);

        licenciatxt.setInputType(InputType.TYPE_CLASS_TEXT );
        licenciatxt.setText(licencia);


        builder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                IP=(inputIP.getText().length()>0)?inputIP.getText().toString().trim():IP;
                licencia=(licenciatxt.getText().length()>0)?licenciatxt.getText().toString().trim():licencia;
                setPreferencias();
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        String str="";
        super.onActivityResult(paramInt1, paramInt2, paramIntent);

        if(paramInt1==3){
            Terminal = Build.SERIAL.toString();
            IP = paramIntent.getStringExtra("ipdirection");
            licencia = paramIntent.getStringExtra("licencia");
            restServiceG = new RestService(mHandlerGet, this, IP+"/apiterminal/ConsultaSaldo?terminal="+Terminal, RestService.POST); //Create new rest service for get
            restServiceG.addHeader("Content-Type","application/json");

            /*restServiceP= new RestService(mHandlerP, this, IP+"/apiterminal/SolicitaAutorizacion?terminal="+Terminal, RestService.POST); //Create new rest service for get
            restServiceP.addHeader("Content-Type","application/json");*/

            /*restServicePp = new RestService(mHandlerPostPos,this,IP+"/apiterminal/ConfigPosiciones?terminal="+Terminal,RestService.POST); //CRETAE new rest for get
            restServicePp.addHeader("Content-Type","application/json");

            restServicePp.execute();*/
        }

        if (paramInt1 == 322) {
            String response=paramIntent.getStringExtra("response");
            System.out.println("----"+Build.DEVICE.toLowerCase()+response);
            try{
                JSONObject jsonObject = new JSONObject(response);
                if(!jsonObject.getString("TrxResult").toString().equalsIgnoreCase("ERROR")){
                    ValidaCuenta(jsonObject.getString("TrxCard").toString());
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this.ctx);
                    builder.setTitle("Error");
                    builder.setIcon(R.drawable.error);
                    builder.setMessage(jsonObject.getString("TrxDescription").toString());
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface param2DialogInterface, int param2Int) {}
                    });
                    builder.show();

                }



            }catch(JSONException e){
                e.printStackTrace();

            }

            return;
        }
        if (paramInt1 == 0) {
            System.out.println(paramIntent.getStringExtra("response"));
            return;
        }
        if (!Build.DEVICE.toLowerCase().contains("wpos")) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(paramInt1, paramInt2, paramIntent);
            if (intentResult != null) {
                str = intentResult.getContents();
                if (str == null) {
                    FragmentUtils.showError((Context)this, "Debes escanear tu Código");
                    return;
                }
                ValidaCuenta(str);
                return;
            }
        } else if (paramInt1 == 0) {
            if (paramInt2 == -1) {
                str =paramIntent.getStringExtra("SCAN_RESULT");

            }
            if (paramInt2 != -1) {
                FragmentUtils.showError((Context)this, "No fue posible recuperar el Código");
                return;
            }
        }
    }

    private void scanQr() {
        try {
            if (!Build.DEVICE.toLowerCase().contains("wpos")) {
                IntentIntegrator intentIntegrator = new IntentIntegrator((Activity)this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Escanea tu Código");
                //intentIntegrator.setResultDisplayDuration(0L);
                intentIntegrator.addExtra("RESULT_DISPLAY_DURATION_MS", 8000L);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.setCameraId(0);
                intentIntegrator.initiateScan();
            } else {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (Exception exception) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.zxing.client.android")));
                }
            }
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private String getJSON(String response) {
        try{
            String json="";
            JSONObject jsonObject = new JSONObject(response);
            json = jsonObject.getString("TrxCard").toString();
            System.out.println("valor...." + json);
        return json;
        }catch(JSONException e){
            e.printStackTrace();
            return "";
        }
    }





}
