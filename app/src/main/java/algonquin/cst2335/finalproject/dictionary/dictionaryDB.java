package algonquin.cst2335.finalproject.dictionary;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class dictionaryDB {

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    @ColumnInfo(name="term")
    protected String term;
    @ColumnInfo(name="definition")
    protected String definition;

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name="id")
    public int id;


    public  dictionaryDB(String term, String definition){
        this.term = term;
        this.definition = definition;

    }

}