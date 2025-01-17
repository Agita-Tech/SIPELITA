package id.saijaansmartdev.sipelita.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import id.saijaansmartdev.sipelita.R;
import id.saijaansmartdev.sipelita.adapter.PelatihanAdapter;
import id.saijaansmartdev.sipelita.helper.ClientHelper;
import id.saijaansmartdev.sipelita.helper.UserPreferences;
import id.saijaansmartdev.sipelita.model.Pelatihan;
import id.saijaansmartdev.sipelita.model.response.PelatihanResponse;
import id.saijaansmartdev.sipelita.network.BapelkesPelitaApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelatihanActivity extends AppCompatActivity {

    private RecyclerView rcPelatihanList;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelatihan);

        showLoading();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toolbar_transparent));

        rcPelatihanList = (RecyclerView) findViewById(R.id.rc_pelatihan);
        rcPelatihanList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(PelatihanActivity.this);
        rcPelatihanList.setLayoutManager(layoutManager);

        listDataRequest();
    }

    private void showLoading() {
        mLoading = new ProgressDialog(PelatihanActivity.this);
        mLoading.setMessage("Mohon tunggu ....");
        mLoading.show();
    }

    private void listDataRequest() {
        BapelkesPelitaApi api = ClientHelper.getClient().create(BapelkesPelitaApi.class);
        Call<PelatihanResponse> pelatihanResponseCall = api.pelatihanResponse(UserPreferences.getKeyUserType(this)+" "+UserPreferences.getKeyUserToken(this));

        pelatihanResponseCall.enqueue(new Callback<PelatihanResponse>() {
            @Override
            public void onResponse(Call<PelatihanResponse> call, Response<PelatihanResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        setListDataPelatihan(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<PelatihanResponse> call, Throwable t) {

            }
        });
    }

    private void setListDataPelatihan(ArrayList<Pelatihan> list) {
        mLoading.dismiss();
        PelatihanAdapter adapter = new PelatihanAdapter(list);
        rcPelatihanList.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PelatihanActivity.this, HomeActivity.class));
        finish();
    }
}
