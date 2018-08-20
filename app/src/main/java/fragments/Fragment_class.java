package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smartschool.smartschooli.R;

import adapter.Fragment_class_listView_adapter;

//课堂界面
public class Fragment_class extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_class_layout,container,false);
        ListView listView=(ListView)view.findViewById(R.id.fragment_class_listView);
        listView.setAdapter(new Fragment_class_listView_adapter(getActivity()));
        return view;
    }

}
