import java.util.ArrayList;
import java.util.HashMap;

public class NetworkPrinter
{
  private ArrayList<ArrayList<Node>> layers;
  
  public ArrayList<ArrayList<Node>> GetLayers()
  {
    return this.layers;
  }
  
  private Genome genome;
  
  public void SetGenome(Genome g)
  {
    this.genome = g;
    this.Recalculate();
  }
  
  public void Recalculate()
  {
    if(this.genome != null)
    {
      this.layers = new ArrayList<ArrayList<Node>>();
      
      // seperating all of the nodes into layers starting from the inputlayer
      
      ArrayList<Node> inputnodes = this.genome.FindNodesOfType(Nodetype.Input);
      
      HashMap<Node, Integer> layermap = new HashMap<Node, Integer>();
      
      for(Node node : inputnodes)
      {
        InsertNode(layermap, node, 1);
      }
      
      ArrayList<Node> outputnodes = this.genome.FindNodesOfType(Nodetype.Output);
      int layercount = 0;
      
      for(Node key_ : layermap.keySet())
      {
        int layer = layermap.get(key_);
        if(layer > layercount)
        {
          layercount = layer;
        }
      }
      
      layercount++;
      
      for(Node node : outputnodes)
      {
        InsertNode(layermap, node, layercount);
      }
            
      for(int i = 0; i < layercount; i++)
      {
        this.layers.add(new ArrayList<Node>());
      }
      
      for(Node key_ : layermap.keySet())
      {
        int index = layermap.get(key_) - 1;
        ArrayList<Node> nodelayer = this.layers.get(index);
        nodelayer.add(key_);
      }
    }
  }
  
  private void InsertNode(HashMap<Node, Integer> layermap, Node nodetoinsert, int layer)
  {
    if(layermap.containsKey(nodetoinsert))
    {
      int layerofnode = layermap.get(nodetoinsert);
      if(layerofnode <= layer)
      {
        InsertNode2(layermap, nodetoinsert, layer);
      }
    }
    else
    {
      InsertNode2(layermap, nodetoinsert, layer);
    }
  }
  
  private void InsertNode2(HashMap<Node, Integer> layermap, Node nodetoinsert, int layer)
  {
    layermap.put(nodetoinsert, layer);
    ArrayList<Connection> connections = this.genome.FindConnectionsFromNode(nodetoinsert.GetID());
    for(Connection connection : connections)
    {
      Node nodenextlayer = this.genome.GetNode(connection.GetTargetNodeID());
      if(nodenextlayer.GetType() == Nodetype.Hidden) // Output Nodes are added last
      {
        InsertNode(layermap, nodenextlayer, layer + 1);  
      }
    }
  }
  
  public NetworkPrinter(Genome g)
  {
    this.SetGenome(g);
    this.Recalculate();
  }
  
  public void Print()
  {
    String str = "";
    int layer = 1;
    for(ArrayList<Node> list : this.layers)
    {      
      str += "---------------------------------------------\n";
      str += "Layer: " + layer + "\n";
      
      for(Node n : list)
      {
        str += n;
      }
      
      layer++;
    }
    
    System.out.println(str == "" ? "no data" : str + "---------------------------------------------\n");
  }
}
