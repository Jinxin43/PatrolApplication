package com.example.dingtu2.myapplication.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.dingtu2.myapplication.MainActivity;
import com.example.dingtu2.myapplication.R;
import com.example.dingtu2.myapplication.model.ManagerBean;
import java.util.List;

public class PatroManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Context mContext;
    private final String mType;
    public ContentLoadingProgressBar contentLoadingProgressBar;
    private List<ManagerBean> mData;
    private final static int TYPE_CONTENT=0;//正常内容
    private final static int TYPE_FOOTER=1;//加载View
    private boolean mIsoffice;


    public PatroManagerAdapter(Context context, String mType) {
        this.mContext = context;
        this.mType=mType;
    }
    @Override
    public int getItemViewType(int position) {
        if (position==mData.size()){
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_foot, parent, false);
            return new FootViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (getItemViewType(position)==TYPE_FOOTER){

        } else{
            MyViewHolder holders= (MyViewHolder) holder;
            ManagerBean bean = mData.get(position);
            if(bean.getEndtime()==null||bean.getEndtime().equals("null")){
                holders.mTvTime.setText(bean.getStarttime().replace("T"," "));
            }else{
                holders.mTvTime.setText(bean.getStarttime().replace("T"," ") + "\n" + bean.getEndtime().replace("T"," "));
            }
            holders.mContent.setText(bean.getPatrolusername());
            holders.mItemPeople.setText(bean.getName());
            if(bean.getDescription()!=null){
                holders.mTvDescribe.setText(bean.getDescription());
            }else{
                holders.mTvDescribe.setText("");
            }
            holders.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mData!=null&&mData.size()>0) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("trackId", mData.get(position).getId());
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                    }

                }
            });
            if (bean.getType() == 21) {
                holders.mTvStyle.setText(mContext.getResources().getString(R.string.ordinary_round));
            } else if (bean.getType() == 22) {
                holders.mTvStyle.setText(mContext.getResources().getString(R.string.check_round));
            } else if (bean.getType() == 23) {
                holders.mTvStyle.setText(mContext.getResources().getString(R.string.duty_round));
            }
            if(mType.equals("my")){
                holders.mAccount.setVisibility(View.GONE);
            }else if(mType.equals("all")){
                holders.mAccount.setVisibility(View.VISIBLE);
            }else if((mType.equals("manager"))){
                holders.mAccount.setVisibility(View.VISIBLE);
            }

        }


    }



    @Override
    public int getItemCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size()+1;
        } else {
            return 0;
        }

    }

    public void setData(List<ManagerBean> mRouteBean, boolean IsOffice) {
        this.mData = mRouteBean;
        this.mIsoffice=IsOffice;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTime, mContent,mItemPeople, mTvWeather, mTvDescribe,mTvStyle;
        private LinearLayout mAccount;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAccount=(LinearLayout)itemView.findViewById(R.id.ll_all_history);
            mTvTime = (TextView) itemView.findViewById(R.id.item_time);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mItemPeople=(TextView)itemView.findViewById(R.id.item_people);
            mTvWeather=(TextView)itemView.findViewById(R.id.tv_weather);
            mTvDescribe=(TextView)itemView.findViewById(R.id.tv_describe);
            mTvStyle=(TextView)itemView.findViewById(R.id.tv_style);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder{
        public FootViewHolder(View itemView) {
            super(itemView);
            contentLoadingProgressBar=(ContentLoadingProgressBar)itemView.findViewById(R.id.pb_progress);
            contentLoadingProgressBar.setVisibility(View.GONE);
        }
    }
}

