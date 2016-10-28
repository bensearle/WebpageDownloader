/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadwebpages;

/**
 * @author bensearle
 */
public class Pair<L,R> {
    private L left;
    private R right;
    public Pair(L l, R r){
        left = l;
        right = r;
    }
    public L getL(){ return left; }
    public R getR(){ return right; }
    public void setL(L l){ left = l; }
    public void setR(R r){ right = r; }
}