package com.vindroid.szbus;

import android.os.AsyncTask;
import android.util.Log;

import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.source.BusParserInterface;
import com.vindroid.szbus.source.szbus.SzBusParser;
import com.vindroid.szbus.source.szbusv2.SzBusV2Parser;
import com.vindroid.szbus.utils.Config;

public class BusCenter {
    private static final String TAG;

    static {
        TAG = App.getTag(BusCenter.class.getSimpleName());
    }

    public interface SearchListener {
        void onSearchCompleted(boolean result, SearchResult searchResult, String msg);
    }

    public interface GetBusLineListener {
        void onGetBusLineCompleted(boolean result, BusLineDetail busLineDetail, String msg);
    }

    public interface GetStationListener {
        void onGetStationCompleted(boolean result, StationDetail stationDetail, String msg);
    }

    public interface GetBusLineRealTimeInfoListener {
        void onGetBusLineRealTimeInfoCompleted(boolean result, BusLineRealTimeInfo info, String msg);
    }

    public static class SearchTask extends AsyncTask<String, Integer, Boolean> {
        SearchListener mListener;
        SearchResult mSearchResult;

        public SearchTask(SearchListener listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String keyword = strings[0];
            try {
                mSearchResult = getParser().search(keyword);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "[SearchTask] has exception", e);
                mSearchResult = new SearchResult();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(TAG, "[SearchTask] result: " + result + ", " + mSearchResult.getType());
            if (mListener != null) {
                mListener.onSearchCompleted(result, mSearchResult, null);
            }
        }
    }

    public static class GetBusLine extends AsyncTask<String, Integer, Boolean> {
        private BusLineDetail mBusLine;
        private final GetBusLineListener mListener;

        public GetBusLine(GetBusLineListener listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String id = strings[0];
            try {
                mBusLine = getParser().getBusLine(id);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "[GetBusLine] has exception", e);
                mBusLine = new BusLineDetail();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(TAG, "[getBusLine] result: " + result
                    + ", name: " + mBusLine.getName() + ", id: " + mBusLine.getId());
            if (mListener != null) {
                mListener.onGetBusLineCompleted(result, mBusLine, null);
            }
        }
    }

    public static class GetStation extends AsyncTask<String, Integer, Boolean> {
        private StationDetail mStation;
        private final GetStationListener mListener;

        public GetStation(GetStationListener listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String id = strings[0];
            try {
                mStation = getParser().getStation(id);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "[GetStation] has exception", e);
                mStation = new StationDetail();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(TAG, "[getStation] result: " + result
                    + ", name: " + mStation.getName() + ", id: " + mStation.getId());
            if (mListener != null) {
                mListener.onGetStationCompleted(result, mStation, null);
            }
        }
    }

    public static class GetBusLineRealTimeInfo extends AsyncTask<String, Integer, Boolean> {
        private BusLineRealTimeInfo mInfo;
        private final GetBusLineRealTimeInfoListener mListener;

        public GetBusLineRealTimeInfo(GetBusLineRealTimeInfoListener listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String id = strings[0];
            try {
                mInfo = getParser().getBusLineRealTimeInfo(id);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "[GetBusLineRealTimeInfo] has exception", e);
                mInfo = new BusLineRealTimeInfo();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(TAG, "[GetBusLineRealTimeInfo] result: " + result
                    + ", next depart: " + mInfo.getNextDepartTime()
                    + ", bus size: " + mInfo.getRunningBuses().size());
            if (mListener != null) {
                mListener.onGetBusLineRealTimeInfoCompleted(result, mInfo, null);
            }
        }
    }

    private static BusParserInterface getParser() {
        if (Config.getSource() == Config.Source.SzBusV2) {
            return new SzBusV2Parser();
        } else {
            return new SzBusParser();
        }
    }
}
