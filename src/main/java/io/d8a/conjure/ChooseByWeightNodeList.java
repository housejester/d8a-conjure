package io.d8a.conjure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A node of a particular weight should be generated at the same rate as the expressed desired weight relative
 * to all the other weights in the list.
 * ( node 'n' generated count / total generations ) = ( node 'n' weight / sum of all weights )
 * A node 'n' of weight 40 in a list with total weight sum of 400 should be generated 40 / 400 times, which is
 * 10%.  So we can expect that when we call generate on the node list 2000 times, we should see about 200 of
 * them be for our node 'n' of weight 40.
 */
public class ChooseByWeightNodeList extends NodeList{
    private static final Random RAND = new Random();

    private List<Integer> weightTiers = new ArrayList<Integer>();
    private int sumOfWeights = 0;

    public void add(ConjureTemplateNode node, int weight){
        add(new WeightedNode(node, weight));
    }

    @Override
    protected void generateNonEmpty(StringBuilder buff){
        updateWeights();
        int ticket = RAND.nextInt(sumOfWeights);
        for(int i = 0; i < weightTiers.size(); i++){
            if(ticket < weightTiers.get(i)){
                nodes.get(i).generate(buff);
                return;
            }
        }
    }

    public int getSumOfWeights(){
        return sumOfWeights;
    }

    private void updateWeights(){
        if(nodes.size() != weightTiers.size()){
            for(int i = weightTiers.size(); i < nodes.size(); i++){
                sumOfWeights += getNodeWeight(nodes.get(i));
                weightTiers.add(sumOfWeights);
            }
        }
    }

    private int getNodeWeight(ConjureTemplateNode node){
        if(node instanceof WeightedNode){
            return ((WeightedNode)node).getWeight();
        }
        return 1;
    }

    public static ChooseByWeightNodeList createNode(Map config, ConjureTemplate template){
        ChooseByWeightNodeList nodes = new ChooseByWeightNodeList();
        List list = (List)config.get("list");
        if(list != null){
            for(Object obj : list){
                nodes.add(parseWeightedNode(String.valueOf(obj), template));
            }
        }
        return nodes;
    }

    public static ConjureTemplateNode parseWeightedNode(String line, ConjureTemplate template){
        int weight = 1;
        int index = line.indexOf(':');
        if(index != -1){
            try{
                weight = new Integer(line.substring(0, index).trim());
                line = line.substring(index+1);
            } catch(Exception ex){
            }
        }
        ConjureTemplateNode node = template.parseNodes(line);
        return new WeightedNode(node, weight);
    }
}
