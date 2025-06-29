package com.example.lossreductionsystemandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetFragment extends Fragment
        implements AssetAdapter.OnAssetActionClickListener{

    private static final String ARG_CLIENT_NAME = "client_name";
    private static final String ARG_CLIENT_ID = "client_id";
    private static final String ARG_PROJECT_NAME = "project_name";
    private static final String ARG_PROJECT_ID = "project_id";
    private static final String BASE_URL = "http://10.0.2.2/loss_reduction_backend/";
    private int clientId, projectId;
    private RequestQueue requestQueue;

    private RecyclerView recyclerView;
    private AssetAdapter adapter;
    private List<Asset> allAssets = new ArrayList<>();
    private List<Asset> filteredAssets = new ArrayList<>();

    private TextInputEditText searchEditText;
    private AutoCompleteTextView categorySpinner, statusSpinner,quantitySpinner;
    private FloatingActionButton fabAddAsset;

    public AssetFragment() {
        // Required empty public constructor
    }

    public static AssetFragment newInstance(int clientId, int projectId) {
        AssetFragment fragment = new AssetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLIENT_ID,clientId);
        args.putInt(ARG_PROJECT_ID,projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            clientId = args.getInt(ARG_CLIENT_ID);
            projectId = args.getInt(ARG_PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset, container, false);

        // 1) find views
        recyclerView = view.findViewById(R.id.assetsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        statusSpinner = view.findViewById(R.id.statusSpinner);
        quantitySpinner = view.findViewById(R.id.quantitySpinner);
        fabAddAsset = view.findViewById(R.id.fabAddAsset);

        // 2) RecyclerView + Adapter
        adapter = new AssetAdapter(requireContext(), filteredAssets, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // 3) Wire up search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 4) FAB → AddAssetActivity
        fabAddAsset.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AssetDetailsActivity.class);
            intent.putExtra("client_id",clientId);
            intent.putExtra("project_id",projectId);
            startActivity(intent);
        });

        // 5) Load data
        fetchAssetFromServer();
        return view;
    }

    private void fetchAssetFromServer(){
        String url = BASE_URL
                + "get_assets.php?client_id=" + clientId
                + "&project_id=" + projectId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    allAssets.clear();
                    for (int i = 0; i < response.length(); i++){
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String path = obj.isNull("image_path") ? null : obj.getString("image_path");
                            String fullUrl = (path != null) ? BASE_URL + path : null;
                            Asset asset = new Asset(
                                    obj.getInt("id"),
                                    obj.getString("asset_name"),
                                    obj.getString("category_name"),
                                    obj.getInt("quantity"),
                                    obj.getDouble("value"),
                                    fullUrl,
                                    obj.getString("status_code"),
                                    obj.getString("status_label"),
                                    obj.getString("status_color")
                            );
                            allAssets.add(asset);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    // After load, populate filter dropdown
                    setupDropdown();
                    applyFilters();
                },
                error -> {

                }
        );
        requestQueue.add(request);
    }

    private void setupDropdown(){
        // extract unique categories, Status, quantities
        Set<String> categories = new HashSet<>();
        Set<String> status = new HashSet<>();
        Set<String> quantities = new HashSet<>();
        categories.add("All");
        status.add("All");
        quantities.add("All");

        for (Asset a: allAssets){
            categories.add(a.getCategory());
            status.add(a.statusLabel);
            quantities.add(String.valueOf(a.getQuantity()));
        }

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
          requireContext(),
          android.R.layout.simple_dropdown_item_1line,
          new ArrayList<>(categories)
        );
        categorySpinner.setAdapter(catAdapter);
        categorySpinner.setText("All",false);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
          requireContext(),
          android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(status)
        );
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setText("All",false);

        ArrayAdapter<String> qtyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(quantities)
        );
        quantitySpinner.setAdapter(qtyAdapter);
        quantitySpinner.setText("All",false);

        // when a filter changes, re-apply
        categorySpinner.setOnItemClickListener((p, v, pos, id) -> applyFilters());
        statusSpinner.setOnItemClickListener((p, v, pos, id) -> applyFilters());
        quantitySpinner.setOnItemClickListener((p, v, pos, id)->applyFilters());
    }

    private void applyFilters(){
        String query = searchEditText.getText().toString().trim().toLowerCase();
        String catSel = categorySpinner.getText().toString();
        String statusSel = statusSpinner.getText().toString();
        String qtySel = quantitySpinner.getText().toString();

        filteredAssets.clear();
        for (Asset a: allAssets){
            boolean matchesSearch = a.getName().toLowerCase().contains(query);
            boolean matchesCategory = catSel.equals("All") || a.getCategory().equals(catSel);
            boolean matchesStatus = statusSel.equals("All") || a.getStatusLabel().equals(statusSel);
            boolean matchesQuantity = qtySel.equals("All") || String.valueOf(a.getQuantity()).equals(qtySel);
            if (matchesSearch && matchesCategory && matchesStatus && matchesQuantity){
                filteredAssets.add(a);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void onAssetActionClicked(Asset asset){
        Intent intent = new Intent(requireContext(), AssetDetailsActivity.class);
        intent.putExtra("asset_id", asset.getId());
        startActivity(intent);
    }
}