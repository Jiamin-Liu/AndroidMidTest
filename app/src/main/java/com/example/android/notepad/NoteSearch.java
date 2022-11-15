package com.example.android.notepad;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class NoteSearch extends Activity implements SearchView.OnQueryTextListener
{
    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    /**
     * The columns needed by the cursor adapter
     */
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE//时间
    };

    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "you choose:"+query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search);
        SearchView searchView = findViewById(R.id.search_view);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        listView = findViewById(R.id.list_view);
        sqLiteDatabase = new NotePadProvider.DatabaseHelper(this).getReadableDatabase();
        //设置搜索视图以显示搜索按钮
        searchView.setSubmitButtonEnabled(true);

        //设置此搜索视图中默认显示的提示文本
        searchView.setQueryHint("search");
        searchView.setOnQueryTextListener(this);

    }
    public boolean onQueryTextChange(String string) {//Test改变的时候执行的内容
        String selection1 = NotePad.Notes.COLUMN_NAME_TITLE+" like ? or "+NotePad.Notes.COLUMN_NAME_NOTE+" like ?";//查询条件
        String[] selection2 = {"%"+string+"%","%"+string+"%"};//查询条件参数，配合selection参数使用,%通配多个字符
        //查询数据库中的内容,当我们使用 SQLiteDatabase.query()方法时，就会得到Cursor对象， Cursor所指向的就是每一条数据。
        //managedQuery(Uri, String[], String, String[], String)等同于Context.getContentResolver().query()
        Cursor cursor = sqLiteDatabase.query(
                NotePad.Notes.TABLE_NAME,
                PROJECTION, // 查询返回的列
                selection1,// 作为查询的过滤参数，也就是过滤出符合selection的数据，类似于SQL的Where语句之后的条件选择
                selection2,// 查询条件参数，配合selection参数使用
                null,
                null,
                NotePad.Notes.DEFAULT_SORT_ORDER // Use the default sort order.查询结果的排序方式，按照某个columns来排序，例：String sortOrder = NotePad.Notes.COLUMN_NAME_TITLE
        );
        String[] dataColumns = {
                NotePad.Notes.COLUMN_NAME_TITLE,
                NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
        } ;
        int[] viewIDs = {
                android.R.id.text1,
                android.R.id.text2
        };
        //一个简单的适配器，将游标中的数据映射到布局文件中的TextView控件或者ImageView控件中
        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,//context:上下文
                R.layout.noteslist_item, //layout:布局文件，至少有int[]的所有视图
                cursor,//cursor：游标
                dataColumns,//绑定到视图的数据
                viewIDs//用来展示from数组中数据的视图
        );
        listView.setAdapter(adapter);
        return true;
    }
}