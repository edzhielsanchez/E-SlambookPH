package com.example.e_slambookph;

import  androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView profileImageView;
    private Button uploadImageButton;

    private Spinner spinnerMunicipality;
    private Spinner spinnerBarangay;
    private Spinner spinnerProvince;

    private Button saveButton;
    private EditText editFirstName, editMiddle, editLastName;

    private Map<String, List<String>> barangaysMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        profileImageView = findViewById(R.id.imageProfileView);
        uploadImageButton = findViewById(R.id.uploadImageButton);

        editFirstName = findViewById(R.id.editFirstName);
        editMiddle = findViewById(R.id.editMiddle);
        editLastName = findViewById(R.id.editLastName);
        saveButton = findViewById(R.id.saveButton);

        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerMunicipality = findViewById(R.id.spinnerMunicipality);
        spinnerBarangay = findViewById(R.id.spinnerBarangay);

        // Initialize barangays for each municipality
        initializeBarangaysMap();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateActivity.this, ListActivity.class);

                startActivity(intent);
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    showImagePickerDialog();
                } else {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                }
            }
        });

        ArrayAdapter<CharSequence> municipalityAdapter = ArrayAdapter.createFromResource(
                this, R.array.municipalities_array, R.layout.spinner_drop_layout);
        municipalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipality.setAdapter(municipalityAdapter);

        // Set listener for Spinner1 (Municipality) selection change
        spinnerMunicipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMunicipality = parentView.getSelectedItem().toString();
                populateBarangaysSpinner(selectedMunicipality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });

        // Populate Spinner3 (Province) with "Bulacan"
        spinnerProvince = findViewById(R.id.spinnerProvince);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_drop_layout, new String[]{"Bulacan"});
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Take Photo option selected
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        // Choose from Gallery option selected
                        openGallery();
                        break;
                    case 2:
                        // Cancel option selected, do nothing
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        // Set the image to the ImageView
                        profileImageView.setImageURI(selectedImageUri);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initializeBarangaysMap() {
        barangaysMap = new HashMap<>();
        // Populate barangays for each municipality
        barangaysMap.put("Angat", getBarangaysForAngat());
        barangaysMap.put("Balagtas", getBarangaysForBalagtas());
        barangaysMap.put("Baliuag", getBarangaysForBaliuag());
        barangaysMap.put("Bocaue", getBarangaysForBocaue());
        barangaysMap.put("Bulakan", getBarangaysForBulakan());
        barangaysMap.put("Bustos", getBarangaysForBustos());
        barangaysMap.put("Calumpit", getBarangaysForCalumpit());
        barangaysMap.put("Doña Remedios Trinidad", getBarangaysForDRT());
        barangaysMap.put("Guiguinto", getBarangaysForGuiguinto());
        barangaysMap.put("Hagonoy", getBarangaysForHagonoy());
        barangaysMap.put("Malolos", getBarangaysForMalolos());
        barangaysMap.put("Marilao", getBarangaysForMarilao());
        barangaysMap.put("Meycauayan", getBarangaysForMeycauayan());
        barangaysMap.put("Norzagaray", getBarangaysForNorzagaray());
        barangaysMap.put("Obando", getBarangaysForObando());
        barangaysMap.put("Pandi", getBarangaysForPandi());
        barangaysMap.put("Paombong", getBarangaysForPaombong());
        barangaysMap.put("Plaridel", getBarangaysForPlaridel());
        barangaysMap.put("Pulilan", getBarangaysForPulilan());
        barangaysMap.put("San Ildefonso", getBarangaysForSanIldefonso());
        barangaysMap.put("San Jose del Monte", getBarangaysForSJDM());
        barangaysMap.put("San Miguel", getBarangaysForSanMiguel());
        barangaysMap.put("San Rafael", getBarangaysForSanRafael());
        barangaysMap.put("Santa Maria", getBarangaysForSantaMaria());
    }

    // Method to populate Spinner2 (Barangay) based on selected municipality
    private void populateBarangaysSpinner(String selectedMunicipality) {
        List<String> barangays = barangaysMap.get(selectedMunicipality);
        if (barangays != null) {
            ArrayAdapter<String> barangayAdapter = new ArrayAdapter<>(
                    this, R.layout.spinner_drop_layout, barangays);
            barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBarangay.setAdapter(barangayAdapter);
        }
    }

    // Method to get barangays for Angat
    private List<String> getBarangaysForAngat() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Banaban");
        barangays.add("Baybay");
        barangays.add("Binagbag");
        barangays.add("Donacion");
        barangays.add("Encanto");
        barangays.add("Laog");
        barangays.add("Marungko");
        barangays.add("Niugan");
        barangays.add("Paltok");
        barangays.add("Pulong Yantok");
        barangays.add("San Roque");
        barangays.add("Santa Cruz");
        barangays.add("Santa Lucia");
        barangays.add("Santo Cristo");
        barangays.add("Sulucan");
        barangays.add("Taboc");
        return barangays;
    }

    // Method to get barangays for Balagtas
    private List<String> getBarangaysForBalagtas() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Borol 1st");
        barangays.add("Borol 2nd");
        barangays.add("Dalig");
        barangays.add("Longos");
        barangays.add("Panginay");
        barangays.add("Pulong Gubat");
        barangays.add("San Juan");
        barangays.add("Santol");
        barangays.add("Wawa");
        return barangays;
    }

    private List<String> getBarangaysForBaliuag() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagong Nayon");
        barangays.add("Barangca");
        barangays.add("Calantipay");
        barangays.add("Catulinan");
        barangays.add("Concepcion");
        barangays.add("Hinukay");
        barangays.add("Makinabang");
        barangays.add("Matangtubig");
        barangays.add("Pagala");
        barangays.add("Paitan");
        barangays.add("Piel");
        barangays.add("Pinagbarilan");
        barangays.add("Poblacion");
        barangays.add("Sabang");
        barangays.add("San Jose");
        barangays.add("San Roque");
        barangays.add("Santa Barbara");
        barangays.add("Santo Cristo");
        barangays.add("Santo Niño");
        barangays.add("Subic");
        barangays.add("Sulivan");
        barangays.add("Tangos");
        barangays.add("Tarcan");
        barangays.add("Tiaong");
        barangays.add("Tibag");
        barangays.add("Tilapayong");
        barangays.add("Virgen delas Flores");
        return barangays;
    }

    private List<String> getBarangaysForBocaue() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Antipona");
        barangays.add("Bagumbayan");
        barangays.add("Bambang");
        barangays.add("Batia");
        barangays.add("Biñang 1st");
        barangays.add("Biñang 2nd");
        barangays.add("Bolacan");
        barangays.add("Bundukan");
        barangays.add("Bunlo");
        barangays.add("Caingin");
        barangays.add("Duhat");
        barangays.add("Igulot");
        barangays.add("Lolomboy");
        barangays.add("Poblacion");
        barangays.add("Sulucan");
        barangays.add("Taal");
        barangays.add("Tambobong");
        barangays.add("Turo");
        barangays.add("Wakas");
        return barangays;
    }

    private List<String> getBarangaysForBulakan() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagumbayan");
        barangays.add("Balubad");
        barangays.add("Bambang");
        barangays.add("Matungao");
        barangays.add("Maysantol");
        barangays.add("Perez");
        barangays.add("Pitpitan");
        barangays.add("San Francisco");
        barangays.add("San Jose");
        barangays.add("San Nicolas");
        barangays.add("Santa Ana");
        barangays.add("Santa Ines");
        barangays.add("Taliptip");
        barangays.add("Tibig");
        return barangays;
    }

    private List<String> getBarangaysForBustos() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bonga Mayor");
        barangays.add("Bonga Menor");
        barangays.add("Buisan");
        barangays.add("Camachilihan");
        barangays.add("Cambaog");
        barangays.add("Catacte");
        barangays.add("Liciada");
        barangays.add("Malamig");
        barangays.add("Malawak");
        barangays.add("Poblacion");
        barangays.add("San Pedro");
        barangays.add("Talampas");
        barangays.add("Tanawan");
        barangays.add("Tibagan");
        return barangays;
    }

    private List<String> getBarangaysForCalumpit() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Balite");
        barangays.add("Balungao");
        barangays.add("Buguion");
        barangays.add("Bulusan");
        barangays.add("Calizon");
        barangays.add("Calumpang");
        barangays.add("Caniogan");
        barangays.add("Corazon");
        barangays.add("Frances");
        barangays.add("Gatbuca");
        barangays.add("Gugo");
        barangays.add("Iba Este");
        barangays.add("Iba O'Este");
        barangays.add("Longos");
        barangays.add("Meysulao");
        barangays.add("Meyto");
        barangays.add("Palimbang");
        barangays.add("Panducot");
        barangays.add("Pio Cruzcosa");
        barangays.add("Poblacion");
        barangays.add("Pungo");
        barangays.add("San Jose");
        barangays.add("San Marcos");
        barangays.add("San Miguel");
        barangays.add("Santa Lucia");
        barangays.add("Santo Niño");
        barangays.add("Sapang Bayan");
        barangays.add("Sergio Bayan");
        barangays.add("Sucol");
        return barangays;
    }

    private List<String> getBarangaysForDRT() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bayabas");
        barangays.add("Camachile");
        barangays.add("Camachin");
        barangays.add("Kabayunan");
        barangays.add("Kalawakan");
        barangays.add("Pulong Sampalok");
        barangays.add("Sapang Bulak");
        barangays.add("Talbak");
        return barangays;
    }

    private List<String> getBarangaysForGuiguinto() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Cutcut");
        barangays.add("Daungan");
        barangays.add("Ilang-Ilang");
        barangays.add("Malis");
        barangays.add("Panginay");
        barangays.add("Poblacion");
        barangays.add("Pritil");
        barangays.add("Pulong Gubat");
        barangays.add("Santa Cruz");
        barangays.add("Santa Rita");
        barangays.add("Tabang");
        barangays.add("Tabe");
        barangays.add("Tiaong");
        barangays.add("Tuktukan");
        return barangays;
    }

    private List<String> getBarangaysForHagonoy() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Abulalas");
        barangays.add("Carillo");
        barangays.add("Iba");
        barangays.add("Iba-Ibayo");
        barangays.add("Mercado");
        barangays.add("Palapat");
        barangays.add("Pugad");
        barangays.add("Sagrada Familia");
        barangays.add("San Agustin");
        barangays.add("San Isidro");
        barangays.add("San Jose");
        barangays.add("San Juan");
        barangays.add("San Miguel");
        barangays.add("San Nicolas");
        barangays.add("San Pablo");
        barangays.add("San Pascual");
        barangays.add("San Pedro");
        barangays.add("San Roque");
        barangays.add("San Sebastian");
        barangays.add("Santa Cruz");
        barangays.add("Santa Elena");
        barangays.add("Santa Monica");
        barangays.add("Santo Niño");
        barangays.add("Santo Rosario");
        barangays.add("Tampok");
        barangays.add("Tibaguin");
        return barangays;
    }

    private List<String> getBarangaysForMalolos() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Anilao");
        barangays.add("Atlag");
        barangays.add("Babatnin");
        barangays.add("Bagna");
        barangays.add("Bagong Bayan");
        barangays.add("Balayong");
        barangays.add("Balite");
        barangays.add("Bangkal");
        barangays.add("Barihan");
        barangays.add("Bulihan");
        barangays.add("Bungahan");
        barangays.add("Caingin");
        barangays.add("Calero");
        barangays.add("Caliligawan");
        barangays.add("Canalate");
        barangays.add("Caniogan");
        barangays.add("Catmon");
        barangays.add("Cofradia");
        barangays.add("Dakila");
        barangays.add("Guinhawa");
        barangays.add("Ligas");
        barangays.add("Liyang");
        barangays.add("Longos");
        barangays.add("Look 1st");
        barangays.add("Look 2nd");
        barangays.add("Lugam");
        barangays.add("Mabolo");
        barangays.add("Mambog");
        barangays.add("Masile");
        barangays.add("Matimbo");
        barangays.add("Mojon");
        barangays.add("Namayan");
        barangays.add("Niugan");
        barangays.add("Pamarawan");
        barangays.add("Panasahan");
        barangays.add("Pinagbakahan");
        barangays.add("San Agustin");
        barangays.add("San Gabriel");
        barangays.add("San Juan");
        barangays.add("San Pablo");
        barangays.add("San Vicente");
        barangays.add("Santiago");
        barangays.add("Santisima Trinidad");
        barangays.add("Santo Cristo");
        barangays.add("Santo Niño");
        barangays.add("Santo Rosario");
        barangays.add("Santol");
        barangays.add("Sumapang Bata");
        barangays.add("Sumapang Matanda");
        barangays.add("Taal");
        barangays.add("Tikay");
        return barangays;
    }

    private List<String> getBarangaysForMarilao() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Abangan Norte");
        barangays.add("Abangan Sur");
        barangays.add("Ibayo");
        barangays.add("Lambakin");
        barangays.add("Lias");
        barangays.add("Loma de Gato");
        barangays.add("Nagbalon");
        barangays.add("Patubig");
        barangays.add("Poblacion I");
        barangays.add("Poblacion II");
        barangays.add("Prenza I");
        barangays.add("Prenza II");
        barangays.add("Santa Rosa I");
        barangays.add("Santa Rosa II");
        barangays.add("Saog");
        barangays.add("Tabing Ilog");
        return barangays;
    }

    private List<String> getBarangaysForMeycauayan() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagbaguin");
        barangays.add("Bahay Pare");
        barangays.add("Bancal");
        barangays.add("Banga");
        barangays.add("Bayugo");
        barangays.add("Caingin");
        barangays.add("Calvario");
        barangays.add("Camalig");
        barangays.add("Hulo");
        barangays.add("Iba");
        barangays.add("Langka");
        barangays.add("Lawa");
        barangays.add("Libtong");
        barangays.add("Liputan");
        barangays.add("Longos");
        barangays.add("Malhacan");
        barangays.add("Pajo");
        barangays.add("Pandayan");
        barangays.add("Pantoc");
        barangays.add("Perez");
        barangays.add("Poblacion");
        barangays.add("Saint Francis");
        barangays.add("Saluysoy");
        barangays.add("Tugatog");
        barangays.add("Ubihan");
        barangays.add("Zamora");
        return barangays;
    }

    private List<String> getBarangaysForNorzagaray() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bangkal");
        barangays.add("Baraka");
        barangays.add("Bigte");
        barangays.add("Bitungol");
        barangays.add("Friendship Village Resources");
        barangays.add("Matictic");
        barangays.add("Minuyan");
        barangays.add("Partida");
        barangays.add("Pinagtulayan");
        barangays.add("Poblacion");
        barangays.add("San Lorenzo");
        barangays.add("San Mateo");
        barangays.add("Tigbe");
        return barangays;
    }

    private List<String> getBarangaysForObando() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Binuangan");
        barangays.add("Catanghalan");
        barangays.add("Hulo");
        barangays.add("Lawa");
        barangays.add("Paco");
        barangays.add("Pag-asa");
        barangays.add("Paliwas");
        barangays.add("Panghulo");
        barangays.add("Salambao");
        barangays.add("San Pascual");
        barangays.add("Tawiran");
        return barangays;
    }

    private List<String> getBarangaysForPandi() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagbaguin");
        barangays.add("Bagong Barrio");
        barangays.add("Baka-bakahan");
        barangays.add("Bunsuran I");
        barangays.add("Bunsuran II");
        barangays.add("Bunsuran III");
        barangays.add("Cacarong Bata");
        barangays.add("Cacarong Matanda");
        barangays.add("Cupang");
        barangays.add("Malibong Bata");
        barangays.add("Malibong Matanda");
        barangays.add("Manatal");
        barangays.add("Mapulang Lupa");
        barangays.add("Masagana");
        barangays.add("Masuso");
        barangays.add("Pinagkuartelan");
        barangays.add("Poblacion");
        barangays.add("Real de Cacarong");
        barangays.add("San Roque");
        barangays.add("Santo Niño");
        barangays.add("Siling Bata");
        barangays.add("Siling Matanda");
        return barangays;
    }

    private List<String> getBarangaysForPaombong() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Binakod");
        barangays.add("Kapitangan");
        barangays.add("Malumot");
        barangays.add("Masukol");
        barangays.add("Pinalagdan");
        barangays.add("Poblacion");
        barangays.add("San Isidro I");
        barangays.add("San Isidro II");
        barangays.add("San Jose");
        barangays.add("San Roque");
        barangays.add("San Vicente");
        barangays.add("Santa Cruz");
        barangays.add("Santo Niño");
        barangays.add("Santo Rosario");
        return barangays;
    }

    private List<String> getBarangaysForPlaridel() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Agnaya");
        barangays.add("Bagong Silang");
        barangays.add("Banga I");
        barangays.add("Banga II");
        barangays.add("Bintog");
        barangays.add("Bulihan");
        barangays.add("Culianin");
        barangays.add("Dampol");
        barangays.add("Lagundi");
        barangays.add("Lalangan");
        barangays.add("Lumang Bayan");
        barangays.add("Parulan");
        barangays.add("Poblacion");
        barangays.add("Rueda");
        barangays.add("San Jose");
        barangays.add("Santa Ines");
        barangays.add("Santo Niño");
        barangays.add("Sipat");
        barangays.add("Tabang");
        return barangays;
    }

    private List<String> getBarangaysForPulilan() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Balatong A");
        barangays.add("Balatong B");
        barangays.add("Cutcot");
        barangays.add("Dampol I");
        barangays.add("Dampol II-A");
        barangays.add("Dampol II-B");
        barangays.add("Dulong Malabon");
        barangays.add("Inaon");
        barangays.add("Longos");
        barangays.add("Lumbac");
        barangays.add("Paltao");
        barangays.add("Penabatan");
        barangays.add("Poblacion");
        barangays.add("Santa Peregrina");
        barangays.add("Santo Cristo");
        barangays.add("Taal");
        barangays.add("Tabon");
        barangays.add("Tibag");
        barangays.add("Tinejero");
        return barangays;
    }

    private List<String> getBarangaysForSanIldefonso() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Akle");
        barangays.add("Alagao");
        barangays.add("Anyatam");
        barangays.add("Bagong Barrio");
        barangays.add("Basuit");
        barangays.add("Bubulong Malaki");
        barangays.add("Bubulong Munti");
        barangays.add("Buhol na Mangga");
        barangays.add("Bulusukan");
        barangays.add("Calasag");
        barangays.add("Calawitan");
        barangays.add("Casalat");
        barangays.add("Gabihan");
        barangays.add("Garlang");
        barangays.add("Lapnit");
        barangays.add("Maasim");
        barangays.add("Makapilapil");
        barangays.add("Malipampang");
        barangays.add("Mataas na Parang");
        barangays.add("Matimbubong");
        barangays.add("Nabaong Garlang");
        barangays.add("Palapala");
        barangays.add("Pasong Bangkal");
        barangays.add("Pinaod");
        barangays.add("Poblacion");
        barangays.add("Pulong Tamo");
        barangays.add("San Juan");
        barangays.add("Santa Catalina Bata");
        barangays.add("Santa Catalina Matanda");
        barangays.add("Sapang Dayap");
        barangays.add("Sapang Putik");
        barangays.add("Sapang Putol");
        barangays.add("Sumandig");
        barangays.add("Telepatio");
        barangays.add("Umpucan");
        barangays.add("Upig");
        return barangays;
    }

    private List<String> getBarangaysForSJDM() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Assumption");
        barangays.add("Bagong Buhay");
        barangays.add("Bagong Buhay II");
        barangays.add("Bagong Buhay III");
        barangays.add("Citrus");
        barangays.add("Ciudad Real");
        barangays.add("Dulong Bayan");
        barangays.add("Fatima");
        barangays.add("Fatima II");
        barangays.add("Fatima III");
        barangays.add("Fatima IV");
        barangays.add("Fatima V");
        barangays.add("Francisco Homes-Guijo");
        barangays.add("Francisco Homes-Mulawin");
        barangays.add("Francisco Homes-Narra");
        barangays.add("Francisco Homes-Yakal");
        barangays.add("Gaya-gaya");
        barangays.add("Graceville");
        barangays.add("Gumaoc Central");
        barangays.add("Gumaoc East");
        barangays.add("Gumaoc West");
        barangays.add("Kaybanban");
        barangays.add("Kaypian");
        barangays.add("Lawang Pari");
        barangays.add("Maharlika");
        barangays.add("Minuyan");
        barangays.add("Minuyan II");
        barangays.add("Minuyan III");
        barangays.add("Minuyan IV");
        barangays.add("Minuyan Proper");
        barangays.add("Minuyan V");
        barangays.add("Muzon");
        barangays.add("Paradise III");
        barangays.add("Poblacion");
        barangays.add("Poblacion I");
        barangays.add("Saint Martin de Porres");
        barangays.add("San Isidro");
        barangays.add("San Manuel");
        barangays.add("San Martin");
        barangays.add("San Martin II");
        barangays.add("San Martin III");
        barangays.add("San Martin IV");
        barangays.add("San Pedro");
        barangays.add("San Rafael");
        barangays.add("San Rafael I");
        barangays.add("San Rafael III");
        barangays.add("San Rafael IV");
        barangays.add("San Rafael V");
        barangays.add("San Roque");
        barangays.add("Santa Cruz");
        barangays.add("Santa Cruz II");
        barangays.add("Santa Cruz III");
        barangays.add("Santa Cruz IV");
        barangays.add("Santa Cruz V");
        barangays.add("Santo Cristo");
        barangays.add("Santo Niño");
        barangays.add("Santo Niño II");
        barangays.add("Sapang Palay");
        barangays.add("Tungkong Mangga");
        return barangays;
    }

    private List<String> getBarangaysForSanMiguel() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagong Pag-asa");
        barangays.add("Bagong Silang");
        barangays.add("Balaong");
        barangays.add("Balite");
        barangays.add("Bantog");
        barangays.add("Bardias");
        barangays.add("Baritan");
        barangays.add("Batasan Bata");
        barangays.add("Batasan Matanda");
        barangays.add("Biak-na-Bato");
        barangays.add("Biclat");
        barangays.add("Buga");
        barangays.add("Buliran");
        barangays.add("Bulualto");
        barangays.add("Calumpang");
        barangays.add("Cambio");
        barangays.add("Camias");
        barangays.add("Ilog-Bulo");
        barangays.add("King Kabayo");
        barangays.add("Labne");
        barangays.add("Lambakin");
        barangays.add("Magmarale");
        barangays.add("Malibay");
        barangays.add("Maligaya");
        barangays.add("Mandile");
        barangays.add("Masalipit");
        barangays.add("Pacalag");
        barangays.add("Paliwasan");
        barangays.add("Partida");
        barangays.add("Pinambaran");
        barangays.add("Poblacion");
        barangays.add("Pulong Bayabas");
        barangays.add("Pulong Duhat");
        barangays.add("Sacdalan");
        barangays.add("Salacot");
        barangays.add("Salangan");
        barangays.add("San Agustin");
        barangays.add("San Jose");
        barangays.add("San Juan");
        barangays.add("San Vicente");
        barangays.add("Santa Ines");
        barangays.add("Santa Lucia");
        barangays.add("Santa Rita Bata");
        barangays.add("Santa Rita Matanda");
        barangays.add("Sapang");
        barangays.add("Sibul");
        barangays.add("Tartaro");
        barangays.add("Tibagan");
        barangays.add("Tigpalas");
        return barangays;
    }

    private List<String> getBarangaysForSanRafael() {
        List<String> barangays = new ArrayList<>();
        barangays.add("BMA-Balagtas");
        barangays.add("Banca-banca");
        barangays.add("Caingin");
        barangays.add("Capihan");
        barangays.add("Coral na Bato");
        barangays.add("Cruz na Daan");
        barangays.add("Dagat-dagatan");
        barangays.add("Diliman I");
        barangays.add("Diliman II");
        barangays.add("Libis");
        barangays.add("Lico");
        barangays.add("Maasim");
        barangays.add("Mabalas-balas");
        barangays.add("Maguinao");
        barangays.add("Maronguillo");
        barangays.add("Paco");
        barangays.add("Pansumaloc");
        barangays.add("Pantubig");
        barangays.add("Pasong Bangkal");
        barangays.add("Pasong Callos");
        barangays.add("Pasong Intsik");
        barangays.add("Pinacpinacan");
        barangays.add("Poblacion");
        barangays.add("Pulo");
        barangays.add("Pulong Bayabas");
        barangays.add("Salapungan");
        barangays.add("Sampaloc");
        barangays.add("San Agustin");
        barangays.add("San Roque");
        barangays.add("Sapang Pahalang");
        barangays.add("Talacsan");
        barangays.add("Tambubong");
        barangays.add("Tukod");
        barangays.add("Ulingao");
        return barangays;
    }

    private List<String> getBarangaysForSantaMaria() {
        List<String> barangays = new ArrayList<>();
        barangays.add("Bagbaguin");
        barangays.add("Balasing");
        barangays.add("Buenavista");
        barangays.add("Bulac");
        barangays.add("Camangyanan");
        barangays.add("Catmon");
        barangays.add("Cay Pombo");
        barangays.add("Caysio");
        barangays.add("Guyong");
        barangays.add("Lalakhan");
        barangays.add("Mag-asawang Sapa");
        barangays.add("Mahabang Parang");
        barangays.add("Manggahan");
        barangays.add("Parada");
        barangays.add("Poblacion");
        barangays.add("Pulong Buhangin");
        barangays.add("San Gabriel");
        barangays.add("San Jose Patag");
        barangays.add("San Vicente");
        barangays.add("Santa Clara");
        barangays.add("Santa Cruz");
        barangays.add("Silangan");
        barangays.add("Tabing Bakod");
        barangays.add("Tumana");
        return barangays;
    }
}