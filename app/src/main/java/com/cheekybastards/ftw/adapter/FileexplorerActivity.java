package com.cheekybastards.ftw.adapter;
  
import android.os.Bundle; 
import android.app.Activity; 
import android.content.Intent; 
import android.view.View;
import android.widget.EditText;
import com.cheekybastards.ftw.fragments.*; 
import com.cheekybastards.ftw.R;
public class FileexplorerActivity extends Activity {
 
	private static final int REQUEST_PATH = 1;
 
	String curFileName;
	
	EditText edittext;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileexplorer); 
        edittext = (EditText)findViewById(R.id.editText);
    }
 
    public void getfile(View view){ 
    	Intent intent1 = new Intent(this, RootFileFragment.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
 // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	if (requestCode == REQUEST_PATH){
    		if (resultCode == RESULT_OK) { 
    			curFileName = data.getStringExtra("GetFileName"); 
            	edittext.setText(curFileName);
    		}
    	 }
    }
}
