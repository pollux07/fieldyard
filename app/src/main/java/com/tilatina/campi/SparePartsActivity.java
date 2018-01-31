package com.tilatina.campi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tilatina.campi.Utilities.CommonUtilities;
import com.tilatina.campi.Utilities.RecyclerItemClickListener;
import com.tilatina.campi.Utilities.SparePartsAdapter;
import com.tilatina.campi.Utilities.SparePartsObjects;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SparePartsActivity extends AppCompatActivity {
    public static final String EVENT_ID = "event_id";
    public static final String ELEMENT_TYPE = "element_type";

    Context mCtx;
    ActionMode mActionMode;
    SwipeRefreshLayout mSwipeRefresh;
    RecyclerView mRecyclerView;
    FloatingActionButton mFab;

    ArrayList<SparePartsObjects> partsList = new ArrayList<>();
    ArrayList<SparePartsObjects> partsSelected = new ArrayList<>();
    SparePartsAdapter partsAdapter;
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> mArrayItemsIds = new ArrayList<>();
    ArrayList<String> mArrayItemsConcept = new ArrayList<>();
    ArrayList<String> mPartsId = new ArrayList<>();

    String mEventId;
    int mPartsNumber;
    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_parts);
        mCtx = this;

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_parts);
        mEventId = getIntent().getStringExtra("event_id");
        getAddedSpareParts(mEventId);

        mRecyclerView = (RecyclerView) findViewById(R.id.added_spare_parts);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.VISIBLE);

        partsAdapter = new SparePartsAdapter(this,partsList,partsSelected);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(partsAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isSelected) {
                    multiSelect(position);
                }

                if (partsSelected.size() == 0) {
                    mFab.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                invalidateOptionsMenu();
                if (!isSelected) {
                    partsSelected = new ArrayList<>();
                    isSelected = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multiSelect(position);

                mFab.setVisibility(View.GONE);
            }
        }));



        DividerItemDecoration dividerItemDecoration
                = new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mArrayAdapter = new ArrayAdapter<>(mCtx,
                android.R.layout.simple_spinner_item,
                mArrayItemsConcept);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newSpareParts(mCtx);
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPartsId.clear();
                isSelected = false;
                invalidateOptionsMenu();
                getAddedSpareParts(mEventId);
                refreshAdapter();
            }
        });
    }

    private void multiSelect(int position) {
        final SparePartsObjects partsObject = partsList.get(position);
        String partId = partsObject.getSparePartsId();

        if (partsSelected.contains(partsList.get(position))) {
            partsSelected.remove(partsList.get(position));
            mPartsId.remove(partId);
        } else {
            partsSelected.add(partsList.get(position));
            mPartsId.add(partId);
        }

        refreshAdapter();
    }

    private void newSpareParts(Context mCtx) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(mCtx);
        alerBuilder.setMessage("Agregar Refaccion(es)");
        alerBuilder.setView(R.layout.spare_parts);
        alerBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = alerBuilder.create();
        dialog.show();
        Spinner spinner = (Spinner) dialog.findViewById(R.id.list_items);
        assert spinner != null;
        final EditText partsQuantity = (EditText) dialog.findViewById(R.id.items_quantity);
        assert partsQuantity != null;
        final String elementType = getIntent().getStringExtra("element_type");
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mArrayAdapter);
        final String[] sparePartsID = {Arrays.toString(new String[1])};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sparePartsID[0] = mArrayItemsIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partsQuantity.setError(null);
                boolean cancel = false;
                View focusView = null;

                String quantityText = partsQuantity.getText().toString();

                if (TextUtils.isEmpty(quantityText)) {
                    partsQuantity.setError(getString(R.string.error_field_required));
                    focusView = partsQuantity;
                    cancel = true;
                } else if (quantityText.contains("00") || quantityText.equals("0")) {
                    partsQuantity.setError(getString(R.string.error_quantity_expectation));
                    focusView = partsQuantity;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    String quantity = Integer
                            .parseInt(quantityText) < 10
                            ? quantityText.replace("0", "")
                            : quantityText;
                    setSpareParts(sparePartsID[0], quantity, mEventId, elementType);
                    dialog.dismiss();
                }
            }
        });
    }

    private void getAddedSpareParts(String eventId) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefresh.setRefreshing(true);
        final TextView withoutParts = (TextView) findViewById(R.id.without_part);
        withoutParts.setVisibility(View.GONE);
        WebService.getAddedParts(mCtx, eventId, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    getSpareParts();
                    JSONObject jsonResponse = new JSONObject(response);
                    partsList.clear();
                    partsAdapter.notifyDataSetChanged();
                    JSONArray jsonArray = jsonResponse.getJSONArray("parts");

                    mPartsNumber = jsonArray.length();
                    if (mPartsNumber == 0) {
                        withoutParts.setVisibility(View.VISIBLE);
                        mSwipeRefresh.setRefreshing(false);
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            SparePartsObjects parts = new SparePartsObjects();
                            parts.setSparePartsId(object.getString("part_id"))
                                    .setSparePartsName(object.getString("part_concept"));
                            partsList.add(parts);
                        }
                        mSwipeRefresh.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError() {
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);

            }
        });
    }

    private void getSpareParts() {
        WebService.getSpareParts(mCtx, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                mArrayAdapter.clear();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int code = jsonResponse.getInt("code");
                    if (code == CommonUtilities.RESPONSE_OK) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemsObject = jsonArray.getJSONObject(i);
                            String itemId = itemsObject.getString("concept_id");
                            String concept = itemsObject.getString("concept");
                            mArrayItemsIds.add(itemId);
                            mArrayItemsConcept.add(concept);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpareParts(String sparePartsID,
                               String quantity,
                               String eventId,
                               String elementType) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefresh.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("concept_id", sparePartsID);
        params.put("event_id", eventId);
        params.put("quantity_parts", quantity);
        params.put("element_type", elementType);

        WebService.setSpareParts(mCtx, params, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int code = jsonResponse.getInt("code");
                    if (code == CommonUtilities.RESPONSE_OK) {
                        getAddedSpareParts(mEventId);
                        mSwipeRefresh.setRefreshing(false);
                        Toast.makeText(mCtx, "Refacción(es) añadida(s)", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError() {
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    private void deleteSpareParts() {
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefresh.setRefreshing(true);
        String listParts = TextUtils.join(",", mPartsId);
        Map <String, String> params = new HashMap<>();
        params.put("parts_ids", listParts);

        WebService.deleteSpareParts(mCtx, params, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int code = jsonResponse.getInt("code");
                    if (code == CommonUtilities.RESPONSE_OK) {
                        mPartsId.clear();
                        isSelected = false;
                        mFab.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        refreshAdapter();
                        getAddedSpareParts(mEventId);
                        mSwipeRefresh.setRefreshing(false);
                        Toast.makeText(mCtx, "Refacción(es) eliminada(s)", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError() {
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isSelected = false;
            partsSelected = new ArrayList<>();
            refreshAdapter();
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.delete);
        if (isSelected) {
            item.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteSpareParts();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshAdapter()
    {
        partsAdapter.selectedPartsList = partsSelected;
        partsAdapter.partsList = partsList;
        partsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (mPartsId.size() > 0){
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);

                startActivity(intent);
                overridePendingTransition(0, 0);

            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
