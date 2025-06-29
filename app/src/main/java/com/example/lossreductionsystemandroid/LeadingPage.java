package com.example.lossreductionsystemandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;

public class LeadingPage extends AppCompatActivity {
    private MaterialButton btnGo;
    private AutoCompleteTextView spinnerClient, spinnerProject;
    private ArrayAdapter<Client> clientAdapter;
    private ArrayAdapter<Project> projectAdapter;
    private ArrayList<Client> clientList = new ArrayList<>();
    private ArrayList<Project> projectList = new ArrayList<>();
    RequestQueue requestQueue;

    private Client   selectedClient;
    private Project  selectedProject;

    private static final String BASE_URL = "http://10.0.2.2/loss_reduction_backend/";
    private static final String GET_CLIENT_URL = BASE_URL + "get_clients.php";
    private static final String GET_PROJECT_URL = BASE_URL + "get_projects.php?client_id=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leading_page);

        requestQueue = Volley.newRequestQueue(this);
        bindViews();
        setupAdapters();
        loadClients();

        spinnerClient.setOnItemClickListener((parent, view, position, id)->{
            selectedClient = clientList.get(position);
            selectedProject = null;
            spinnerProject.setText("",false);
            projectList.clear();
            projectAdapter.notifyDataSetChanged();
            btnGo.setEnabled(false);
            loadProjects(selectedClient.id);
        });

        spinnerProject.setOnItemClickListener((parent, view, position, id) -> {
            selectedProject = projectList.get(position);
            Toast.makeText(this, "Selected Project: " + selectedProject.name, Toast.LENGTH_SHORT).show();
        });

        btnGo.setOnClickListener(v -> onGoClicked());
    }

    private void bindViews(){
        btnGo = findViewById(R.id.btnGo);
        spinnerClient = findViewById(R.id.spinnerClient);
        spinnerProject = findViewById(R.id.spinnerProject);
    }

    private void setupAdapters(){
        clientAdapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                clientList
        );
        spinnerClient.setAdapter(clientAdapter);

        projectAdapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                projectList
        );
        spinnerProject.setAdapter(projectAdapter);
    }

    private void loadClients(){
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                GET_CLIENT_URL,
                null,
                response -> {
                    clientList.clear();
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject clientObject = response.getJSONObject(i);
                            int id = clientObject.getInt("id");
                            String name = clientObject.getString("name");
                            clientList.add(new Client(id,name));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    clientAdapter.notifyDataSetChanged();
                    btnGo.setEnabled(false);
                },
                error -> Toast.makeText(this,"Error loading clients",Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void loadProjects(int clientId){
        String url = GET_PROJECT_URL + clientId;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    projectList.clear();
                    for(int i = 0; i<response.length(); i++){
                        try {
                            JSONObject projectObject = response.getJSONObject(i);
                            int id = projectObject.getInt("id");
                            String name = projectObject.getString("name");
                            projectList.add(new Project(id,name));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    projectAdapter.notifyDataSetChanged();
                    // only enable Go once both lists have something
                    btnGo.setEnabled(!projectList.isEmpty());
                },
                error -> Toast.makeText(this,"Error loading projects",Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void onGoClicked(){
        if (selectedClient == null || selectedProject == null) {
            Toast.makeText(this,"Please select both a client and a project", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this,TabsActivity.class);
        intent.putExtra("client_id",   selectedClient.id);
        intent.putExtra("client_name", selectedClient.name);
        intent.putExtra("project_id",  selectedProject.id);
        intent.putExtra("project_name",selectedProject.name);
        startActivity(intent);
        finish();
    }
}