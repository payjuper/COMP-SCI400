import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
public class Backend implements BackendInterface {
    private final IterableSortedCollection<Song> tree;

    private Integer curLow = null;
    private Integer curHigh = null; // energy range

    private Integer curDanceThreshold = null; // dance filter threshold

    public Backend(IterableSortedCollection<Song> tree) {
        this.tree = tree;
    }
    public static Comparator<Song> energyOrder() {
        return new Comparator<Song>() {
            @Override   
            public int compare(Song a, Song b) {
                int byEnerge = Integer.compare(a.getEnergy(), b.getEnergy()); // conpare by energy first
                if(byEnerge == 0) {
                    return a.getTitle().compareTo(b.getTitle());
                }
                return byEnerge;
            }
        };
    }

    @Override
    public void readData(String filename) throws IOException
    {
        try(BufferedReader br = new BufferedReader((new FileReader(filename)))){ // read file first line check the column
            String head = br.readLine();
            if(head == null) {
                return ;
            }
            String[] Cols = head.split(","); 

            HashMap<String, Integer> idx = new HashMap<>();

            for (int i = 0; i < Cols.length; i++) {
                idx.put(Cols[i].trim().toLowerCase(), i);   // map column name to index because the order may change        
            }
            Integer iTitle = idx.get("title");
            Integer iYear = idx.get("year");
            Integer iEnergy = idx.get("nrgy");
            Integer iDance = idx.get("dnce");
            Integer iArtist = idx.get("artist");
            Integer iGenre  = idx.get("top genre");
            Integer iBpm    = idx.get("bpm");
            Integer iLoud   = idx.get("val");
            Integer iLive   = idx.get("live");

            String line;
            while ((line = br.readLine()) != null) { // read each line, if there is '"', ignore the ',' in it
                List<String> columnsList = new ArrayList<>();
                int subStringBegin = 0; 
                boolean flag = false;
                if (line.trim().isEmpty()) continue;
                for(int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    if(ch == '"') {
                        if(!flag) {
                            flag = !flag; // flag means we are in the " "
                            subStringBegin = i ; //set the begin index
                            continue;
                        }
                        else {
                            flag = !flag; // if " is right " means we conme out of " "
                        }    
                    }   
                    else if(ch == ',' && !flag) { // if find "," meanwhile we are not in " "
                        String token = line.substring(subStringBegin, i);
                        columnsList.add(token);
                        subStringBegin = i + 1; // set the begin index of next column
                    }
                }
                String last = line.substring(subStringBegin);
                columnsList.add(last); // add the last column
                String title = columnsList.get(iTitle).trim();
                int year     = Integer.parseInt(columnsList.get(iYear).trim());
                int energy   = Integer.parseInt(columnsList.get(iEnergy).trim());
                int dance    = Integer.parseInt(columnsList.get(iDance).trim());
                String artist = columnsList.get(iArtist).trim();
                String genre  = columnsList.get(iGenre).trim();    
                int bpm       = Integer.parseInt(columnsList.get(iBpm).trim());
                int loud      = Integer.parseInt(columnsList.get(iLoud).trim());
                int live      = Integer.parseInt(columnsList.get(iLive).trim()); // combine columns and idx
                Song s = new Song(title, artist, genre, year, bpm, energy, dance, loud, live, energyOrder());
                tree.insert(s);
            }
        }
    }
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        this.curLow = low;
        this.curHigh = high;// set energy range in backend
        List<String> res = new ArrayList<>();
        for(Song s : tree) {
            if(inEnergyRange(s, low, high) && passDance(s, this.curDanceThreshold)) { // if in range and pass dance filter
                res.add(s.getTitle());
            }
        }
        return res;
    }

    @Override
    public List<String> applyAndSetFilter(Integer threshold) {
        List<String> res = new ArrayList<>();
        this.curDanceThreshold = threshold; // set dance filter in backend
        for(Song s : tree) {
            if(inEnergyRange(s, this.curLow, this.curHigh) && passDance(s, this.curDanceThreshold)) {// if in range and pass dance filter
                res.add(s.getTitle());
            }
        }
        return res;
    }
    @Override
    public List<String> fiveMost() {  
        List<Song> res = new ArrayList<>(); 
        List<String> ans = new ArrayList<>(); 
        for(Song s : tree) {
            if(inEnergyRange(s, this.curLow, this.curHigh) && passDance(s, this.curDanceThreshold)) {// if in range and pass dance filter
                res.add(s);
            }
        }
        res.sort((a, b) -> {return Integer.compare(b.getYear(), a.getYear());}); // sort Year in descending order
        for(int i = 0; i < Math.min(res.size(),5); i++) // we only need five most in the list
            ans.add(res.get(i).getTitle());
        return ans;
    }
    private boolean inEnergyRange(Song s, Integer low, Integer high) {
        Integer curEnerge = s.getEnergy();
        if(low != null && curEnerge < low) return false;
        if(high != null && curEnerge > high) return false;
        return true;
    }
    private boolean passDance(Song s, Integer threshold) {
    if (threshold == null) return true;     
    return s.getDanceability() > threshold;     
}
}