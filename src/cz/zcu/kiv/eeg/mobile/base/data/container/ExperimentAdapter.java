package cz.zcu.kiv.eeg.mobile.base.data.container;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom class of ArrayAdapter. Used for viewing Experiment records in ListView.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class ExperimentAdapter extends ArrayAdapter<Experiment> implements Filterable {

    private final Context context;
    private final int resourceId;
    private List<Experiment> original;
    private List<Experiment> filtered;
    private ExperimentListFilter experimentListFilter = new ExperimentListFilter();

    public ExperimentAdapter(Context context, int resourceId, List<Experiment> items) {
        super(context, resourceId);
        this.context = context;
        this.resourceId = resourceId;
        original = new ArrayList<Experiment>(items.size());
        filtered = new ArrayList<Experiment>(items.size());
        for (Experiment exp : items) {
            filtered.add(exp);
            original.add(exp);
        }
    }

    public void add(Experiment object) {
        original.add(object);
        this.notifyDataSetChanged();
    }

    @Override
    public Experiment getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resourceId, parent, false);
        }
        Experiment record = getItem(position);
        if (record != null) {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            TextView topText = (TextView) row.findViewById(R.id.toptext);
            TextView additionalText = (TextView) row.findViewById(R.id.bottomtext);

            if (topText != null) {
                topText.setText(record.getExperimentId() + " | " + record.getScenarioName());
            }
            if (additionalText != null) {
                additionalText.setText(sf.format(record.getStartTime()) + " – " + sf.format(record.getEndTime()));
            }
        }
        return row;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return experimentListFilter;
    }

    private class ExperimentListFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            final Filter.FilterResults oReturn = new Filter.FilterResults();
            final List<Experiment> results = new ArrayList<Experiment>();

            if (constraint == null || constraint.toString().isEmpty()) {
                oReturn.values = original;
                oReturn.count = original.size();
            } else {
                if (original != null) {
                    for (Experiment exp : original) {
                        if (Integer.toString(exp.getExperimentId()).contains(constraint) || exp.getScenarioName().toLowerCase().contains(constraint)) {
                            results.add(exp);
                        }
                    }
                }
                oReturn.values = results;
                oReturn.count = results.size();
            }
            return oReturn;

        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            clear();
            filtered = (List<Experiment>) results.values;
            notifyDataSetChanged();
        }
    }

}
