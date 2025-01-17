package id.saijaansmartdev.sipelita.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import id.saijaansmartdev.sipelita.R;
import id.saijaansmartdev.sipelita.adapter.HistoryTrainingAdapter;
import id.saijaansmartdev.sipelita.helper.ClientHelper;
import id.saijaansmartdev.sipelita.helper.UserPreferences;
import id.saijaansmartdev.sipelita.model.HistoryTraining;
import id.saijaansmartdev.sipelita.model.response.HistoryTrainingResponse;
import id.saijaansmartdev.sipelita.network.BapelkesPelitaApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PemesananTrainingFragment extends Fragment {

    private RecyclerView rcHistoryTrainingList;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<HistoryTraining.Training> historyTrainingArrayList;
    private HistoryTraining historyData;
    private int page = 1;

    public PemesananTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_training, container, false);

        rcHistoryTrainingList = (RecyclerView) view.findViewById(R.id.rc_post_test);
        rcHistoryTrainingList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rcHistoryTrainingList.setLayoutManager(layoutManager);

        trainingHistoryRequest();
        return view;
    }

    private void trainingHistoryRequest() {
        BapelkesPelitaApi api = ClientHelper.getClient().create(BapelkesPelitaApi.class);
        Call<HistoryTrainingResponse> historyTrainingResponseCall = api.historyTrainingResponseCall(UserPreferences.getKeyUserType(getContext())+" "+UserPreferences.getKeyUserToken(getContext()), "antrian");

        historyTrainingResponseCall.enqueue(new Callback<HistoryTrainingResponse>() {
            @Override
            public void onResponse(@NotNull Call<HistoryTrainingResponse> call, @NotNull Response<HistoryTrainingResponse> response) {
                assert response.body() != null;
                historyData = response.body().getData();
                historyTrainingArrayList = historyData.getData();
                setRecyclerTrainingHistory(historyTrainingArrayList);
            }

            @Override
            public void onFailure(@NotNull Call<HistoryTrainingResponse> call, @NotNull Throwable t) {

            }
        });
    }

    private void setRecyclerTrainingHistory(ArrayList<HistoryTraining.Training> data) {
        HistoryTrainingAdapter adapter = new HistoryTrainingAdapter(data);
        rcHistoryTrainingList.setAdapter(adapter);

        rcHistoryTrainingList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisiblesItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    Log.d("_data_", "Visble : "+(visibleItemCount + pastVisiblesItems)+" dan total item : "+totalItemCount);
                    Log.d("_data_", "Current : "+historyData.getCurrent_page()+" dan last : "+historyData.getLast_page());
                    Log.d("_data_", "Current : "+page);
                    if(historyData.getLast_page() > historyData.getCurrent_page()) {
                        page++;
                        BapelkesPelitaApi api = ClientHelper.getClient().create(BapelkesPelitaApi.class);
                        Call<HistoryTrainingResponse> historyTrainingScrollResponseCall = api.historyTrainingScrollResponseCall(UserPreferences.getKeyUserType(getContext()) + " " + UserPreferences.getKeyUserToken(getContext()), "antrian", String.valueOf(page));

                        historyTrainingScrollResponseCall.enqueue(new Callback<HistoryTrainingResponse>() {
                            @Override
                            public void onResponse(Call<HistoryTrainingResponse> call, Response<HistoryTrainingResponse> response) {
                                historyData = response.body().getData();
                                historyTrainingArrayList.addAll(response.body().getData().getData());
                                setRecyclerTrainingHistory(historyTrainingArrayList);
                            }

                            @Override
                            public void onFailure(Call<HistoryTrainingResponse> call, Throwable t) {

                            }
                        });
                    }
                }
            }

        });
    }

}
