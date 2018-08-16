package com.example.cmahajan.flickrgallery.utilities;

import android.util.Log;

import com.example.cmahajan.flickrgallery.models.DataHolder;
import com.example.cmahajan.flickrgallery.models.FlickerItem;

import java.util.ArrayList;

public class Paginator {


    private static final int ITEMS_PER_PAGE = 10;
    private DataHolder dataHolder;
    private int TOTAL_NUM_ITEMS;
    private static final String TAG = "PAGINATOR";

    public Paginator(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        TOTAL_NUM_ITEMS = dataHolder.getFlickerItems().size();
    }

    public int getTotalPages() {
        int remainingItems = TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;
        if (remainingItems > 0) {
            return TOTAL_NUM_ITEMS / ITEMS_PER_PAGE;
        }
        return (TOTAL_NUM_ITEMS / ITEMS_PER_PAGE) - 1;

    }


    public ArrayList<FlickerItem> getCurrentFlickerItems(int currentPage) {
        int startItem = currentPage * ITEMS_PER_PAGE;
        int lastItem = startItem + ITEMS_PER_PAGE;

        ArrayList<FlickerItem> currentFlickerItems = new ArrayList<>();

        //LOOP THROUGH LIST OF FLICKER ITEMS AND FILL CURRENT FLICKER ITEM LIST
        try {
            for (int i = 0; i < TOTAL_NUM_ITEMS; i++) {

                //ADD CURRENT PAGE'S DATA
                if (i >= startItem && i < lastItem) {
                    currentFlickerItems.add(dataHolder.getFlickerItems().get(i));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error fetching current Flicker Items.");
        } finally {
            return currentFlickerItems;
        }
    }

}
