package sgbd;

import engine.file.FileManager;
import engine.file.buffers.OptimizedFIFOBlockBuffer;
import engine.virtualization.interfaces.HeapStorage;
import engine.virtualization.record.Record;
import sgbd.prototype.Column;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.TranslatorApi;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.util.Conversor;

import java.io.IOException;
import java.math.BigInteger;

public class Main {

    public static void main(String[] args) throws IOException {
        //Prepara o protótipo da tabela

        FileManager fm = new FileManager("bd.dat", new OptimizedFIFOBlockBuffer(4));
        HeapStorage heap  = new HeapStorage(fm);

        Prototype p = new Prototype();
        p.addColumn("id",4,Column.PRIMARY_KEY);
        p.addColumn("nome",25000,Column.DINAMIC_COLUMN_SIZE);
        p.addColumn("teste",100,Column.DINAMIC_COLUMN_SIZE);

        p.addColumn("description",16,Column.SHIFT_8_SIZE_COLUMN|Column.DINAMIC_COLUMN_SIZE|Column.CAM_NULL_COLUMN);
        p.addColumn("anoNascimento",4,Column.NONE);
        p.addColumn("email",120,Column.NONE);
        p.addColumn("idade",4,Column.CAM_NULL_COLUMN);
        p.addColumn("cidade",4,Column.PRIMARY_KEY);
        p.addColumn("salario",4,Column.NONE);

        TranslatorApi translator = p.validateColumns();

        RowData row = new RowData();
        row.setInt("id",1);
        row.setString("nome","Luiz Henrique");
        row.setString("teste","Testando");
        row.setString("email","luiz@henrique.com");
        row.setInt("idade",21);
        row.setInt("cidade",1200);
        row.setInt("anoNascimento",2001);
        row.setFloat("salario",15.2f);

        Record r = translator.convertToRecord(row);
        heap.clearFile();

        heap.writeSeq(r.getData(),0,r.size());
        heap.commitWrites();

        RowData row2 =translator.convertToRowData(r);

        for (Column c:
                p) {
            System.out.println("Coluna: "+c.getName()+" Tamanho:"+c.getSize()+" "+((c.isDinamicSize())?"(Dinamic)":(c.isPrimaryKey())?"(Primary key)":""));
        }

        System.out.println(row.getDataInt("id")+" | "+row2.getDataInt("id"));
        System.out.println(row.getDataInt("cidade")+" | "+row2.getDataInt("cidade"));
        System.out.println(row.getDataInt("anoNascimento")+" | "+row2.getDataInt("anoNascimento"));
        System.out.println(row.getDataInt("idade")+" | "+row2.getDataInt("idade"));
        System.out.println(row.getDataFloat("salario")+" | "+row2.getDataFloat("salario"));

        System.out.println(row.getDataString("nome")+" | "+row2.getDataString("nome"));
        System.out.println(row.getDataString("teste")+" | "+row2.getDataString("teste"));
        System.out.println(row.getDataString("email")+" | "+row2.getDataString("email"));
        System.out.println(row.getDataString("description")+" | "+row2.getDataString("description"));


        fm.close();
    }
}
