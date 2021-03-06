package player;

import game_control.BestChildNodeList;
import game_control.Board;
import game_control.GameController;

import javax.swing.*;

public class AIFieldValue implements IPlayer {
    private String name;
    private ImageIcon color;
    private int minimax;
    private int depth;
    private int[][] fieldValues;
    private int iterations = 0;

    @Override
    public Board makeMove(Board currentBoardState) {
        iterations =0;
        return visitFirstNode(depth, -200000 , 200000 , currentBoardState , minimax);
    }

    public Board visitFirstNode(int depth , int alpha , int beta , Board board , int minimaxlevel)
    {
        //System.out.println("ParentBoard -- Depth "+depth +" alpha "+alpha + " beta "+beta+" level "+minimaxlevel+"\n"+ board);
        iterations++;
        BestChildNodeList list = new BestChildNodeList(minimax);
        if(minimaxlevel == GameController.MAXIMIZER)
        {
            while(alpha < beta)
            {
                Board child = board.nextChildNode(minimaxlevel);
                if(child == null)
                    break;
                int v = alphaBeta(depth-1 , alpha , beta , child , minimaxlevel*-1);
                list.insert(child , v);
                if(v > alpha) alpha = v;

            }
        }
        else
        {
            while(alpha < beta)
            {
                Board child = board.nextChildNode(minimaxlevel);
                if(child == null)
                    break;
                int v = alphaBeta(depth-1 , alpha , beta , child , minimaxlevel*-1);
                list.insert(child , v);
                if(v < beta) beta = v;
            }

        }
        System.out.println("ite: "+iterations);
        Board[] b = list.getBestChildNodes();
        list = new BestChildNodeList(minimax);
        for(Board bn : b)
            list.insert(bn , staticEval(bn.board));
        return list.getRandomBestChild();
    }

    private int alphaBeta(int depth , int alpha , int beta , Board board , int minimaxlevel)
    {
        //System.out.println("ParentBoard -- Depth "+depth +" alpha "+alpha + " beta "+beta+" level "+minimaxlevel+"\n"+ board);
        iterations++;
        int s = staticEval(board.board);
        System.out.println("Heuristic: "+s+"\n"+board);
        if(depth == 0 || ((board.board & 511) == 511) || (s == 80 || s == -80))
            return s;

        Board child = null;
        if(minimaxlevel == GameController.MAXIMIZER)
        {
            while(alpha < beta)
            {
                child = board.nextChildNode(minimaxlevel);
                if(child == null)
                    return alpha;
                int v = alphaBeta(depth-1 , alpha , beta , child , minimaxlevel*-1);
                if(v > alpha) alpha = v;
            }
            return alpha;

        }
        else
        {
            while(alpha < beta)
            {
                child = board.nextChildNode(minimaxlevel);
                if(child == null)
                    return beta;
                int v = alphaBeta(depth-1 , alpha , beta , child , minimaxlevel*-1);
                if(v < beta) beta = v;
            }
            return beta;
        }

    }





    private int staticEval(int board)
    {
        int playerAt = board >> 9;
        int valAt0_0 = (((playerAt & 1)*2)-1) * (board&1) * fieldValues[0][0];
        int valAt1_0 = ((playerAt & 2)-1) *(( board & 2 )>>1) * fieldValues[1][0];
        int valAt2_0 = (((playerAt & 4) >> 1)-1) *(( board & 4 )>>2) * fieldValues[2][0];
        int valAt0_1 = (((playerAt & 8) >> 2)-1) *(( board & 8 )>>3) * fieldValues[0][1];
        int valAt1_1 = (((playerAt & 16) >> 3)-1) *(( board & 16 )>>4) * fieldValues[1][1];
        int valAt2_1 = (((playerAt & 32) >> 4)-1) *(( board & 32 )>>5) * fieldValues[2][1];
        int valAt0_2 = (((playerAt & 64) >> 5)-1) *(( board & 64 )>>6) * fieldValues[0][2];
        int valAt1_2 = (((playerAt & 128) >> 6)-1) *(( board & 128 )>>7) * fieldValues[1][2];
        int valAt2_2 = (((playerAt & 256) >> 7)-1) *(( board & 256 )>>8) * fieldValues[2][2];
        //System.out.println(valAt0_0 +","+valAt0_1+","+valAt0_2+","+valAt1_0+","+valAt1_1+","+valAt1_2+","+valAt2_0+","+valAt2_1+","+valAt2_2);

        int row1 =  valAt0_0 + valAt1_0 + valAt2_0;
        int row2 = valAt0_1 + valAt1_1 + valAt2_1;
        int row3 = valAt0_2 + valAt1_2 + valAt2_2;
        int col1 = valAt0_0 + valAt0_1 + valAt0_2;
        int col2 = valAt1_0 + valAt1_1 + valAt1_2;
        int col3 = valAt2_0 + valAt2_1 + valAt2_2;
        int dia1 = valAt0_0 + valAt1_1 + valAt2_2;
        int dia2 = valAt2_0 + valAt1_1 + valAt0_2;



        if(row1 == 8 || row1 == -8)
            return row1*10;
        else if(row2 == 8 ||row2 == -8)
            return row2*10;
        else if(row3 == 8 || row3 == -8)
            return row3*10;
        else if(col1 == 8 || col1 == -8)
            return col1*10;
        else if(col2 == 8 || col2 == -8)
            return col2*10;
        else if(col3 == 8 || col3 == -8)
            return col3*10;
        else if(dia1 == 10 || dia1 == -10)
            return dia1*8;
        else if(dia2==10 ||dia2 == -10)
            return dia2*8;
        int result = valAt0_0;
        result += valAt0_1;
        result += valAt0_2;
        result += valAt1_0;
        result += valAt1_1;
        result += valAt1_2;
        result += valAt2_0;
        result += valAt2_1;
        result += valAt2_2;


        return result;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int minimax() {
        return minimax;
    }

    @Override
    public ImageIcon color() {
        return color;
    }

    public AIFieldValue(String name , ImageIcon color , int minimax, int depth)
    {
        this.name = name;
        this.color = color;
        this.minimax = minimax;
        this.depth = depth;
        this.fieldValues = new int[3][3];
        fieldValues[0][0] = fieldValues[0][2] = fieldValues[2][0] = fieldValues[2][2] = 3;
        fieldValues[0][1] = fieldValues[1][0] = fieldValues[2][1] = fieldValues[1][2] = 2;
        fieldValues[1][1] = 4;


    }
}
