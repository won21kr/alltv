/*
 * MIT License
 *
 * Copyright (c) 2019 PYTHONKOR

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ksi.alltv;

import android.content.Intent;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class OksusuRowSupportFragment extends AllTvBaseRowsSupportFragment implements OnItemViewClickedListener {

    private static final String TAG = OksusuRowSupportFragment.class.getSimpleName();
    private int mCnt = 0;

    private static SettingsData.OksusuQualityType mQualityType = SettingsData.OksusuQualityType.AUTO;

    public OksusuRowSupportFragment() {
        setOnItemViewClickedListener(this);
        mType = Utils.SiteType.Oksusu;
    }

    public static void setChannelList(ArrayList<ChannelData> chList) {
        AllTvBaseRowsSupportFragment.setChannelList(Utils.SiteType.Oksusu, chList);
    }

    public static void setCategoryList(ArrayList<CategoryData> ctList) {
        AllTvBaseRowsSupportFragment.setCategoryList(Utils.SiteType.Oksusu, ctList);
    }

    public static void setAuthKey(String authKey) {
        AllTvBaseRowsSupportFragment.setAuthKey(Utils.SiteType.Oksusu, authKey);
    }

    public static void setQualityType(SettingsData.OksusuQualityType inType) {

        if (mQualityType != inType)
            clearAllVideoUrl();

        mQualityType = inType;
    }

    public static void clearAllVideoUrl() {
        AllTvBaseRowsSupportFragment.clearAllVideoUrl(Utils.SiteType.Oksusu);
    }

    public static void updateFavoriteList(ArrayList<String> favorites) {
        AllTvBaseRowsSupportFragment.updateFavoriteList(Utils.SiteType.Oksusu, favorites);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof ChannelData) {
            String authKey = ((ChannelData) item).getAuthkey();

            if (authKey == null || authKey.length() < 10) {
                Utils.showToast(getContext(), getStringById(R.string.nologin_error));
                return;
            }
            playVideo(mChannels.get(mType).indexOf(item));
        }
    }

    private void playVideo(int currentChannel) {
        if (PlayerActivity.active) return;

        int requestCode = Utils.Code.OksusuPlay.ordinal();
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra(getStringById(R.string.CHANNELS_TAG), mChannels.get(mType));
        intent.putExtra(getStringById(R.string.CURRENTCHANNEL_STR), currentChannel);

        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void createRows() {

        mRowsAdapter.clear();

        if (isEmptyCategory(mType)) {
            createDefaultRows();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
            return;
        }

        CardPresenter presenterSelector = new CardPresenter();

        ArrayList<ChannelData> chList = mChannels.containsKey(mType) ? new ArrayList<>(mChannels.get(mType)) : new ArrayList<>();
        Collections.reverse(chList);

        ArrayList<CategoryData> ctList = mCategory.containsKey(mType) ? mCategory.get(mType) : new ArrayList<>();

        for (CategoryData ctData : ctList) {

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);

            for (int i = (chList.size() - 1); i >= 0; i--) {
                if (ctData.getId() == chList.get(i).getCategoryId()) {
                    adapter.add(chList.get(i));
                    chList.remove(i);
                }
            }

            HeaderItem headerItem = new HeaderItem(ctData.getTitle());

            if (adapter.size() > 0)
                mRowsAdapter.add(new ListRow(headerItem, adapter));
        }

        if (getMainFragmentAdapter() != null)
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void refreshRows() {
        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
    }

    @Override
    public void sendChannelData() {
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra(getStringById(R.string.CHANNELS_TAG), mChannels.get(mType));

        getActivity().startActivity(intent);
    }

}

