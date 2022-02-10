package sgbd.query.basic.binaryop;

import engine.util.Util;
import sgbd.prototype.RowData;
import sgbd.query.basic.Operator;
import sgbd.query.basic.Tuple;
import sgbd.query.basic.sourceop.PKTableScan;
import sgbd.query.basic.sourceop.TableScan;

import java.math.BigInteger;
import java.util.Map;

public class FKNestedLoopJoin extends  BinaryOperator{

    protected Tuple nextTuple=null;

    protected String source,foreignKey;
    private PKTableScan tableScan;
    private boolean showIfNullRightOperator;

    public FKNestedLoopJoin(Operator left, PKTableScan tableScan, String source, String foreignKey) {
        super(left, tableScan);
        this.tableScan=tableScan;
        this.source=source;
        this.showIfNullRightOperator=false;
        this.foreignKey=foreignKey;
    }
    public FKNestedLoopJoin(Operator left, PKTableScan tableScan, String source, String foreignKey, boolean showIfNullRightOperator) {
        super(left, tableScan);
        this.tableScan=tableScan;
        this.source=source;
        this.showIfNullRightOperator=showIfNullRightOperator;
        this.foreignKey=foreignKey;
    }

    @Override
    public void open() {
        left.open();
        nextTuple=null;
    }

    @Override
    public Tuple next() {
        try {
            if(nextTuple==null)findNextTuple();
            return nextTuple;
        }finally {
            nextTuple = null;
        }
    }

    @Override
    public boolean hasNext() {
        findNextTuple();
        return (nextTuple==null)?false:true;
    }
    protected Tuple findNextTuple(){
        if(nextTuple!=null)return nextTuple;

        while(left.hasNext()){
            Tuple leftTuple = left.next();
            byte[] data = leftTuple.getContent(source).getData(foreignKey);
            BigInteger fk = Util.convertByteArrayToNumber(data);
            tableScan.setPrimaryKey(fk);
            tableScan.open();
            if(tableScan.hasNext()){
                Tuple rightTuple = tableScan.next();
                for (Map.Entry<String, RowData> entry:
                     rightTuple) {
                    leftTuple.setContent(entry.getKey(), entry.getValue());
                }
                nextTuple = leftTuple;
                return nextTuple;
            }else if(this.showIfNullRightOperator){
                nextTuple = leftTuple;
                return nextTuple;
            }
            tableScan.close();
        }
        return null;
    }

    @Override
    public void close() {
        nextTuple = null;
        left.close();
    }
}