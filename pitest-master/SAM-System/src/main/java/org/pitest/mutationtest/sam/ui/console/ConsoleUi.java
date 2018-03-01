package org.pitest.mutationtest.sam.ui.console;

import org.pitest.extensions.MutationRandomizerSingleton;
import org.pitest.mutationtest.sam.config.FromFileMetaData;
import org.pitest.mutationtest.sam.config.IProjectMetaData;
import org.pitest.mutationtest.sam.config.ImputMetaData;
import org.pitest.mutationtest.sam.config.SimpleMetaDataToPropagate;
import org.pitest.mutationtest.sam.ui.Iui;
import org.pitest.mutationtest.sam.web.WebSocketWorkerNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by gosc on 19.11.2016.
 */
public class ConsoleUi implements Iui{

    private WebSocketWorkerNode _workerSerwer;
    private  boolean  _isOn;
    private static Object _lock;
    private BufferedReader _cnsl;

    //Comands Strings-------------------------------

    String CCS_stoped = "SAM-System Console stoped";
    String CCS_start ="================================================================"+System.lineSeparator()+
                      "SAM-SYSTEM v 1.0"+System.lineSeparator()+
                      "================================================================"+System.lineSeparator()+
                      "Need help? type 'help'"
            ;
    //Comands Strings-------------------------------


    public ConsoleUi(WebSocketWorkerNode workerSerwer) {
        _workerSerwer = workerSerwer;
        _isOn = true;
        _lock = new Object();
        _cnsl = new BufferedReader(new InputStreamReader(System.in));
        Runnable consoleImput = () -> {consoleImmputTask();};
        consoleImput.run();
    }

    private void consoleImmputTask(){
        try {
            synchronized (_lock){
                System.out.println(CCS_start);
                while (_isOn){
                        String comand = _cnsl.readLine();
                    switch (comand) {
                        case "test":
                           System.out.println("Test wykonany");
                            break;
                        case "connect":
                            System.out.println("Server adress: ");
                            String adress = _cnsl.readLine();
                            System.out.println("Server Port: ");
                            int port = Integer.parseInt(_cnsl.readLine());
                            this.connectTo(adress,port);
                            break;
                        case "start":
                            System.out.println("Set server working port Port: ");
                            int port1 = Integer.parseInt(_cnsl.readLine());
                            this.startSerwer(Integer.valueOf(port1));
                            break;
                        case "run mutation -broudcast":
                            IProjectMetaData tempData =new FromFileMetaData();//i to trzeba jakos ogarnac tutaj zabawa analityczna
                            //Przed wszystkim klasy trzeba wyciac osobno i do testów ilosc klas przez ilosc nodó i wywylayac jakos
                            _workerSerwer.SendToAllConnectedNodes("PitRun", tempData);
                            break;
                        case "run mutation": //Odpalanie pojedynczej instacji pita projekt pobierany z konfiga
                            IProjectMetaData tempDataLocal =new FromFileMetaData();
                           // MutationRandomizerSingleton.SetBayes = true;
                            _workerSerwer.RunnPitStandAlone(tempDataLocal);
                            break;
                        case "run mutation -bayes": //Odpalanie pojedynczej instacji pita projekt pobierany z konfiga z uwzglednienim bayesa

                           int Bayesit =0;
                           while(Bayesit<1){
                               FromFileMetaData tempDataLocal_bayes =new FromFileMetaData(true);
                               for (FromFileMetaData data:tempDataLocal_bayes.GetMetaDataAsAList()) {
                                   System.out.println(".");
                                   System.out.println(".");
                                   System.out.println(".");
                                   System.out.println(".");
                                   System.out.println("-----------------------------------------------------------");
                                   System.out.println("MUTACJA BAYES Dla: "+ data.Classname);
                                   System.out.println("-----------------------------------------------------------");
                                   MutationRandomizerSingleton.SetBayes = true;
                                   MutationRandomizerSingleton.ActualClass =data.Classname;
                                   _workerSerwer.RunnPitStandAlone(data);

                               }
                               Bayesit++;
                           }



                            break;
                        case "run mutation -i": //Metoda z wstryzkiwanym imputem hardcoded narazie
                            //ImputMetaData(String ClassesClassPatch, String TestClassPath, String DumpDir, String ClassesStringList){
                            IProjectMetaData tempDataFromIn =new ImputMetaData(
                                    "D:\\\\Doktorat\\\\PitPlayground\\\\IOgr602-master\\\\target\\\\test-classes\\\\,D:\\\\Doktorat\\\\PitPlayground\\\\IOgr602-master\\\\target\\\\classes\\\\",
                                    "D:\\\\Doktorat\\\\PitPlayground\\\\IOgr602-master\\\\",
                                    "D:\\\\trash\\\\",
                                    "matrixlibrary.IMatrixMathImpl, matrixlibrary.IMatrixImpl"
                            );
                            IProjectMetaData tempDataFromIn2 = new SimpleMetaDataToPropagate(tempDataFromIn.GetMetaDataList(), tempDataFromIn.GetClaspathAsAList());
                            _workerSerwer.SendToAllConnectedNodes("PitRun", tempDataFromIn2);
                            break;
                        case "Send":
                            System.out.println("Message: ");
                            String msg = _cnsl.readLine();
                            _workerSerwer.SendToAllConnectedNodes(msg, null);
                            break;
                        case "help":
                            System.out.println("Message: ");
                            System.out.println("ENG:");

                            System.out.println("Commands:");
                            System.out.println("1. 'test' Internal test not important.");
                            System.out.println("2. 'connect' System will ask you for ip address and port. After setting correct data system will connect to SAM Serwer. (After connection you can send star mutation request for all SAM  Servers).");
                            System.out.println("3. 'start' Run SAM System server on this machine. You will be ask for port.");
                            System.out.println("4. 'run mutation -broudcast' This command send start mutation request to all connected SAM severs.");


                            System.out.println("PL:");

                            System.out.println("Komendy:");
                            System.out.println("1. 'test' Wewnętrzny test sytemu nie istotne.");
                            System.out.println("2. 'connect' System zapyta cię o adres ip. i Port a następnie po podaniu prawidłowych danych połączy cie z nimi. (po połączeniu będziesz mógł wysłać żądanie rozpoczęcia testów mutacyjnych)");
                            System.out.println("3. 'start' Uruchamia serwer mutacyjny na. Należy podać port na jakim serwer będzie działał");
                            System.out.println("4. 'run mutation -broudcast' Ta komendy wysyła broadcastem wszystkim połączonym maszynom komunikat o prośbie rozpoczęcia mutacji.");
                            System.out.println("4. 'run mutation' Ta komendy uruchamia mutacje lokalnie tylko tu");
                            System.out.println("4. 'run mutation -i '  Ta komendy wysyła broadcastem wszystkim połączonym maszynom komunikat o prośbie rozpoczęcia mutacji. zarazem po i potrzeba podać okreslony immput przykład: ");

                            break;

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            _isOn = false;
            System.out.println(CCS_stoped);
        }

    }


    public void killConsole(){
        synchronized (_lock) {
            _isOn = false;
        }
    }


    @Override
    public void startSerwer(int port) {
        _workerSerwer.Start(port);
    }

    @Override
    public void connectTo(String adress, int port) {
        _workerSerwer.ConnectSlaveNode(adress,port);
    }

    @Override
    public void runnPit(IProjectMetaData data) {

    }
}
