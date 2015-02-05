package datahandler;
import utility.CollectionHandler;
import java.util.Arrays;
import dataObjects.*;

public class InputHandler {

    private static boolean debug = true;
    private static final String CURRENT_INSTANCE_DIR = System.getProperty("user.dir") + System.getProperty("file.separator");
    public static GlobalData data= new GlobalData();
    /**
     * Create a new InputHandler object.
     */
    public InputHandler() {
        super();
    }

    public static void readInstance(String filename) {
        readInstance(CURRENT_INSTANCE_DIR, filename);
    }

    public static void readInstance(String path, String name) {
		try {
	        InputReader reader = new InputReader(path, name);
	        while (reader.readLine()) {
	            //No. of agents
	            String line = reader.getLine();
	            String[] parts;
	            if (line.contains("noOfPatrons")) {
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                data.setNoOfPatrons(Integer.parseInt(parts[0].trim()));
	                if (debug) {
	                    System.out.println("numAgents: " + data.getNoOfPatrons());
	                }
	
	            }
	            if (line.contains("noOfNodes")) {
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                data.setNoOfNodes(Integer.parseInt(parts[0].trim()));
	                if (debug) {
	                    System.out.println("numVertices: " + data.getNoOfNodes());
	                }
	            }
	            
	            if (line.contains("noOfRoutesPerK")) {
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                data.setNoOfRoutesPerK(Integer.parseInt(parts[0].trim()));
	                if (debug) {
	                    System.out.println("noOfRoutesPerK: " + data.getNoOfRoutesPerK());
	                }
	            }
	            
	            if (line.contains("noOfTaskNodes")) {
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                data.setNoOfTaskNodes(Integer.parseInt(parts[0].trim()));
	                if (debug) {
	                    System.out.println("numOfTask: " + data.getNoOfTaskNodes());
	                }
	            }
	            if (line.contains("taskNodes")) {
	                int [] tasks = new int[data.getNoOfTaskNodes()];
	                parts = line.split("=");
	                line = parts[1].replace("[", " ");
	                line = line.replace("]", " ");
	                line = line.replace(";", " ");
	                parts = line.split(",");
	                int index = 0;
	                for (String s : parts) {
	                    tasks[index] = Integer.parseInt(s.trim());
	                    index++;
	                }
	                data.setTaskNodes(tasks);
	                if (debug) {
	                    System.out.print("tasks: ");
	                    CollectionHandler.printArrays(tasks);
	                }
	            }
	            if (line.contains("taskUtility")) {
	                double[] taskUtility = new double[data.getNoOfTaskNodes()];
	                parts = line.split("=");
	                line = parts[1].replace("[", " ");
	                line = line.replace("]", " ");
	                line = line.replace(";", " ");
	                parts = line.split(",");
	                int index = 0;
	                for (String s : parts) {
	                	taskUtility[index] = Double.parseDouble(s.trim());
	                    index++;
	                }
	                data.setTaskUtility(taskUtility);
	                if (debug) {
	                    System.out.print("taskUtility: ");
	                    CollectionHandler.printArrays(taskUtility);
	                }
	            }
	            
	            if (line.contains("walkingTimes")) {
	                double[][] distMatrix = new double[data.getNoOfNodes()][data.getNoOfNodes()];
	
	                line = line.split("=")[1];
	                line = line.replace("[", " ");
	                line = line.replace("]", " ");
	                line = line.replace(";", " ");
	                parts = line.split(",");
	                for (int i = 0; i < data.getNoOfNodes(); i++) {
	                    double dist = Double.parseDouble(parts[i].trim());
	                    distMatrix[0][i]=dist;
	                }
	                if (debug) {
	                    System.out.println("walkingTimes:");
	                    System.out.println(Arrays.toString(distMatrix[0]));
	                }
	                for (int x = 1; x < data.getNoOfNodes(); x++) {
	                    if (reader.readLine()) {
	                        line = reader.getLine();
	                        line = line.replace("[", " ");
	                        line = line.replace("]", " ");
	                        line = line.replace(";", " ");
	                        parts = line.split(",");
	                        for (int i = 0; i < data.getNoOfNodes(); i++) {
	                            double dist = Double.parseDouble(parts[i].trim());
	                            distMatrix[x][i]=dist;
	                        }
	                        if (debug) {
	                            System.out.println(Arrays.toString(distMatrix[x]));
	                        }
	
	                    }
	                }
	                data.setWalkingTimes(distMatrix);
	            }
	            
	            if (line.contains("instanceID")) {
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String instanceID = parts[0].trim();
	                instanceID.replace("\"", "");
	                data.setInstanceID(instanceID);
	                if (debug) {
	                    System.out.println("instanceID: " + instanceID);
	                }
	            }
	            if (line.contains("routeProbability")) {
	            	double[][] routeProbability = new double[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
	//            	System.out.println(line);
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String l=parts[0].trim();
	                l=l.replace("[[", "");
	                l=l.replace("]]", "");
	                parts = l.split("] \\[");
	            	for(int i=0;i<parts.length;i++) {
	            		String[] x=parts[i].split(",");
	            		for(int m=0;m<x.length;m++) {
	            			routeProbability[i][m]=Double.parseDouble(x[m].trim());
	            		}
	            	}
	                data.setRouteProbability(routeProbability);
	                if (debug) {
	                    System.out.println("routeProbability: ");
	                    for(double[] s:routeProbability) {
	                    	System.out.println(Arrays.toString(s));
	                    }
	                }
	            }
	            
	            if (line.contains("startNode")) {
	            	int[][] startNode = new int[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
	//            	System.out.println(line);
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String l=parts[0].trim();
	                l=l.replace("[[", "");
	                l=l.replace("]]", "");
	                parts = l.split("] \\[");
	            	for(int i=0;i<parts.length;i++) {
	            		String[] x=parts[i].split(",");
	            		for(int m=0;m<x.length;m++) {
	            			startNode[i][m]=Integer.parseInt(x[m].trim());
	            		}
	            	}
	                data.setStartNode(startNode);
	                if (debug) {
	                    System.out.println("startNode: ");
	                    for(int[] s:startNode) {
	                    	System.out.println(Arrays.toString(s));
	                    }
	                }
	            }
	            
	            if (line.contains("endNode")) {
	            	int[][] endNode = new int[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
	//            	System.out.println(line);
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String l=parts[0].trim();
	                l=l.replace("[[", "");
	                l=l.replace("]]", "");
	                parts = l.split("] \\[");
	            	for(int i=0;i<parts.length;i++) {
	            		String[] x=parts[i].split(",");
	            		for(int m=0;m<x.length;m++) {
	            			endNode[i][m]=Integer.parseInt(x[m].trim());
	            		}
	            	}
	                data.setEndNode(endNode);
	                if (debug) {
	                    System.out.println("endNode: ");
	                    for(int[] s:endNode) {
	                    	System.out.println(Arrays.toString(s));
	                    }
	                }
	            }
	
	            
	            if (line.contains("detourTime")) {
	            	double[][] detourTime = new double[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
	//            	System.out.println(line);
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String l=parts[0].trim();
	                l=l.replace("[[", "");
	                l=l.replace("]]", "");
	                parts = l.split("] \\[");
	            	for(int i=0;i<parts.length;i++) {
	            		String[] x=parts[i].split(",");
	            		for(int m=0;m<x.length;m++) {
	            			detourTime[i][m]=Double.parseDouble(x[m].trim());
	            		}
	            	}
	                data.setDetourTime(detourTime);
	                if (debug) {
	                    System.out.println("detourTime: ");
	                    for(double[] s:detourTime) {
	                    	System.out.println(Arrays.toString(s));
	                    }
	                }
	            }
	            
	            if (line.contains("visitingRoutineNodesSequence")) {
	            	Route[][]  visitingSequence = new Route[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
	            	int[][] noOfRoutineNode=new int[data.getNoOfPatrons()][data.getNoOfRoutesPerK()]; //k,m
//	            	System.out.println(line);
	                parts = line.split("=");
	                parts = parts[1].split(";");
	                String l=parts[0].trim();
	                l=l.replace("[ [ [", "");
	                l=l.replace("] ] ]", "");
	                parts = l.split("] ] \\[ \\[");
	            	for(int k=0;k<parts.length;k++) {
	//                    System.out.println(parts[k]);
	            		String[] x=parts[k].split("] \\[");
	
	            		for(int m=0;m<x.length;m++) {
	                		String[] y=x[m].split(",");
	                		int count=0;
	                		for(String s:y) {
	                			int id=Integer.parseInt(s.trim());
	                			if(id!=0) {
	                    			count++;
	                			}
	                		}
	//            			System.out.println("noOfRoutineNode:"+count);
	                  		Route route= new Route(count);
	            			int nId=0;
	                		for(String s:y) {
	                			int id=Integer.parseInt(s.trim());
	                			if(id!=0) {
	                        		route.getPath().set((id-1), nId);
	//                    			System.out.println(nId+" **"+"index="+(id-1));
	                			}
	                			nId++;
	                		}
	//                		System.out.println(count+"="+route.getPath());
	            			visitingSequence[k][m]=route;
	            			noOfRoutineNode[k][m]=count;
	            		}
	            	}
	                data.setVisitingRoutineNodesSequence(visitingSequence);
	                data.setNoOfRoutineNode(noOfRoutineNode);
	                if (debug) {
	                    System.out.println("visitingRoutineNodesSequence: ");
	                    for(Route[] s:visitingSequence) {
	                    	for(Route r:s) {
	                    		System.out.println(r.getPath());
	                    	}
	                    }
	                }
	            }
	       }
			 System.out.println("Reading instance done.");
		} catch (Exception exc) {
			 System.out.println("Reading instance fail.");
			 System.out.println(exc);
		} 
    }
}
