/*
 * Authors  : Nicolas Fuchs & Grégory Ducrey
 * Date     : 02.03.2018
 */

package test;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.util.regex.*;

public class ListProcesses {

    public static void main(String[] args) {
        try {
            
            String username = System.getProperty("user.name");
            System.out.println("Username detected : " + username);
            
            SystemInfo si = new SystemInfo();
            OperatingSystem os = (OperatingSystem) si.getOperatingSystem();

            Process p = null;
            String operatingSystem = os.getFamily();
            System.out.println(operatingSystem + " OS detected !\n");
            System.out.println("********************************************");
            System.out.println("First way\n");

            if (operatingSystem.equals("Windows")) {
                p = Runtime.getRuntime().exec("tasklist.exe");
            } else if (operatingSystem.equals("MacOS") || operatingSystem.equals("Kali GNU/Linux")) {   //conditions to verify
                p = Runtime.getRuntime().exec("ps -eo comm");    //another command is "top" on mac (live update)
            }

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = ""; int firstCounter = 0;

            if (operatingSystem.equals("Windows")) {
                Pattern pattern = Pattern.compile(".*.exe");
                while((line = input.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) System.out.println(matcher.group(0));
                    firstCounter++;
                }
            } else if (operatingSystem.equals("MacOS") || operatingSystem.equals("Kali GNU/Linux")) {   //conditions to verify
                while((line = input.readLine()) != null) {
                    System.out.println(line);
                    firstCounter++;
                }
            }

            System.out.println("\n Total number of processes : " + firstCounter);
            System.out.println("********************************************");
            System.out.println("Second way\n");

            OSProcess[] processes = os.getProcesses(0,OperatingSystem.ProcessSort.CPU);
            int secondCounter = 0;
            PrintWriter pwn = new PrintWriter(new FileWriter(new File("Processes_Names_list.txt")));
            PrintWriter pwp = new PrintWriter(new FileWriter(new File("Processes_Pathes_list.txt")));
            for (int i = 0; i < processes.length; i++) {
                System.out.println("ProcessName : " + processes[i].getName());
                System.out.println("Path : " + processes[i].getPath());
                System.out.println("ProcessID : " + processes[i].getProcessID());
                System.out.println("ParentProcessID : " + processes[i].getParentProcessID());
                System.out.println(); 
                pwn.write(processes[i].getName());
                pwp.write(processes[i].getPath());
                secondCounter++;
            }
            pwn.close();
            pwp.close();
            System.out.println("\n Total number of processes : " + secondCounter);
        } catch (Exception e) {
            System.out.println("No command line opened");
            e.printStackTrace();
        }
    }
}