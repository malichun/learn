package mongoDB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @description:
 * @author: malichun
 * @time: 2021/3/29/0029 13:34
 */
public class BatchInsertMongoDB {
    private static MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
    private static long count = 1000000;

    private static MongoClient getClient() {
        return mongoClient;
    }

    public static void main(String[] args) {
        List<Document> list = new ArrayList<>();
        MongoCollection<Document> collection1 = mongoClient.getDatabase("fullbook").getCollection("book");


        for (int i = 0; i < 2200000; i++) {
            Document document = new Document();
            document.append("id", String.valueOf(count));
            Date date = new Date();
            document.append("add_time", date);
            document.append("title", "mongo" + count++);
            list.add(document);
            if (i != 0 && i % 10000 == 0) {
                System.out.println("插入" + " " + i + "本书");
                collection1.insertMany(list);
                list.clear();
            }
        }
    }
}

