import com.example.recognitionapp.model.Org;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.model.Recognition;
import com.example.recognitionapp.model.RedemptionHistory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import java.util.EnumSet;
import java.io.File;

public class GenerateSchema {

    public static void main(String[] args) {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(Org.class);
        metadataSources.addAnnotatedClass(User.class);
        metadataSources.addAnnotatedClass(Recognition.class);
        metadataSources.addAnnotatedClass(RedemptionHistory.class);

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setFormat(true); // Format the SQL
        schemaExport.setOutputFile("target/generated-ddl/schema.sql");
        schemaExport.setDelimiter(";");

        // Ensure the output directory exists
        File outputDir = new File("target/generated-ddl");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
        schemaExport.execute(targetTypes, SchemaExport.Action.CREATE, metadataSources.buildMetadata());

        System.out.println("Schema DDL generated to target/generated-ddl/schema.sql");
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }
}
