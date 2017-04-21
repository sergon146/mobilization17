package com.sergon146.mobilization17.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.sergon146.mobilization17.R;
import com.sergon146.mobilization17.pojo.translate.mapper.Def;
import com.sergon146.mobilization17.pojo.translate.mapper.Ex;
import com.sergon146.mobilization17.pojo.translate.mapper.Mean;
import com.sergon146.mobilization17.pojo.translate.mapper.Syn;
import com.sergon146.mobilization17.pojo.translate.mapper.Tr;
import com.sergon146.mobilization17.pojo.translate.mapper.WordMapper;

import java.util.ArrayList;
import java.util.List;

public class MeanLayout {
    private Context context;
    private LayoutInflater li;
    private View.OnClickListener flowListener;

    public MeanLayout(Context context, View.OnClickListener flowListener) {
        this.context = context;
        this.flowListener = flowListener;
        li = LayoutInflater.from(context);
    }

    public List<View> getMeanViews(WordMapper word) {
        String prevTr = "";
        List<View> viewList = new ArrayList<>();
        for (Def def : word.getDef()) {

            viewList.add(getTranslateLayout(def, prevTr));

            List<Tr> trList = def.getTr();
            if (trList != null && !trList.isEmpty()) {
                for (Tr tr : trList) {
                    LinearLayout layout = (LinearLayout) li.inflate(R.layout.mean_view, null, false);
                    setFlow(tr, layout);
                    setMeans(tr, layout);
                    setExes(tr, layout);
                    viewList.add(layout);
                }
            }
        }
        return viewList;
    }

    public LinearLayout getTranslateLayout(Def def, String prevTr) {
        LinearLayout targetLayout = (LinearLayout) li.inflate(R.layout.translate_view, null, false);

        if (!prevTr.equals(def.getText())) {
            if (def.getText() != null) {
                TextView wordOrigin = (TextView) targetLayout.findViewById(R.id.mean_word_orign);
                wordOrigin.setText(def.getText());
            }

            if (def.getTs() != null) {
                TextView wordTranscription = (TextView) targetLayout.findViewById(R.id.mean_word_transcription);
                wordTranscription.setText("[" + def.getTs() + "]");
            }
        } else {
            targetLayout.setVisibility(View.GONE);
        }
        if (def.getPos() != null) {
            TextView partView = (TextView) targetLayout.findViewById(R.id.mean_part);
            if (def.getPos().isEmpty()) {
                partView.setVisibility(View.GONE);
            } else {
                partView.setText(def.getPos());
            }
        }
        return targetLayout;
    }

    private void setFlow(Tr tr, LinearLayout layout) {
        FlowLayout flowLayout = (FlowLayout) layout.findViewById(R.id.mean_flow_layout);
        setTrs(tr, flowLayout);
        setSyns(tr, flowLayout);
    }

    private void setTrs(Tr tr, FlowLayout flowLayout) {
        if (tr.getText() != null) {
            LinearLayout itemLayout = (LinearLayout) li.inflate(R.layout.flow_item_view, null, false);
            if (tr.getSyn() == null || tr.getSyn().isEmpty()) {
                TextView itemEnd = (TextView) itemLayout.findViewById(R.id.flow_item_end);
                itemEnd.setVisibility(View.GONE);
            }
            TextView itemName = (TextView) itemLayout.findViewById(R.id.flow_item_name);
            itemName.setText(tr.getText());
            itemName.setOnClickListener(flowListener);

            TextView itemGen = (TextView) itemLayout.findViewById(R.id.flow_item_gen);
            if (tr.getGen() != null) {
                if (!tr.getGen().isEmpty()) {
                    itemGen.setVisibility(View.VISIBLE);
                    itemGen.setText(tr.getGen());
                }
            }
            flowLayout.addView(itemLayout);
        }
    }

    private void setSyns(Tr tr, FlowLayout flowLayout) {
        List<Syn> synList = tr.getSyn();
        if (synList != null) {
            for (int i = 0; i < synList.size(); i++) {
                Syn syn = synList.get(i);
                if (syn.getText() != null && !syn.getText().isEmpty()) {
                    LinearLayout itemLayout = (LinearLayout) li.inflate(R.layout.flow_item_view, null, false);

                    TextView itemName = (TextView) itemLayout.findViewById(R.id.flow_item_name);
                    itemName.setText(syn.getText());
                    itemName.setOnClickListener(flowListener);

                    if (syn.getGen() != null) {
                        TextView itemGen = (TextView) itemLayout.findViewById(R.id.flow_item_gen);
                        if (!syn.getGen().isEmpty()) {
                            itemGen.setVisibility(View.VISIBLE);
                            itemGen.setText(syn.getGen());
                        }
                    }
                    if (i == synList.size() - 1) {
                        TextView itemEnd = (TextView) itemLayout.findViewById(R.id.flow_item_end);
                        itemEnd.setVisibility(View.GONE);
                    }
                    flowLayout.addView(itemLayout);
                }
            }

        }
    }



    private void setMeans(Tr tr, LinearLayout layout) {
        List<Mean> meanList = tr.getMean();
        if (meanList != null) {
            TextView meanSameView = (TextView) layout.findViewById(R.id.mean_same_view);
            if (!meanList.isEmpty()) {
                meanSameView.setVisibility(View.VISIBLE);
                String mean = "(";
                for (int i = 0; i < meanList.size(); i++) {
                    if (i != 0) {
                        mean += ", ";
                    }
                    mean += meanList.get(i).getText();
                }
                mean += ")";

                meanSameView.setText(mean);
            }
        }
    }

    private void setExes(Tr tr, LinearLayout layout) {
        List<Ex> exList = tr.getEx();
        if (exList != null && !exList.isEmpty()) {
            LinearLayout meanStmLayout = (LinearLayout) layout.findViewById(R.id.mean_stm_layout);
            if (exList.isEmpty()) {
                meanStmLayout.setVisibility(View.GONE);
            } else {
                for (Ex ex : exList) {
                    TextView exView = new TextView(context);
                    exView.setTextColor(ContextCompat.getColor(context, R.color.exColor));
                    exView.setTextSize(18);
                    exView.setTypeface(exView.getTypeface(), Typeface.ITALIC);

                    String exString = ex.getText() + " - ";

                    List<Tr> trExList = ex.getTr();
                    if (trExList != null && !trExList.isEmpty()) {
                        for (int i = 0; i < trExList.size(); i++) {
                            if (i != 0) {
                                exString += "\n";
                            }
                            exString += trExList.get(i).getText();
                        }
                        exView.setText(exString);
                    }
                    meanStmLayout.addView(exView);
                }
            }
        }
    }
}
