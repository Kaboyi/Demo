package example;

import sun.security.provider.SHA;

import java.io.*;
import java.util.*;

/**
 * @author Administrator
 */
public class One
{
    String message = "foo";
    
    public String foo()
    {
        BufferedReader bin = null;
        BufferedWriter bout = null;
        try
        {
            File inFile = new File("in.txt");
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            
            // 第一行是数字,代表线程数
            String firstLine = bin.readLine();
            Integer processNum = Integer.valueOf(firstLine);
            
            // 线程池
            Map<Integer, SortHandler> threadMap = new HashMap<Integer, SortHandler>(1 << 10);
            
            // 读取字符行
            String line;
            for (int i = 0; (line = bin.readLine()) != null; i++)
            {
                int key = i % processNum;
                
                SortHandler handler = threadMap.get(key);
                if (handler == null)
                {
                    handler = new SortHandler();
                    threadMap.put(key, handler);
                }
                
                // 加入线程中等待处理
                handler.add(line);
            }
            
            for (Thread thread : threadMap.values())
            {
                thread.start();
            }
            for (Thread thread : threadMap.values())
            {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 归并结果
            List<String> list = new ArrayList();
            long st = System.currentTimeMillis();
            for (SortHandler rs : threadMap.values())
            {
                List<String> list1 = rs.list();
                list.addAll(list1);
            }
            
            // 排序
            Collections.sort(list);
            
            System.out.println("Time : " + (System.currentTimeMillis() - st));
            
            message = "Sorted the data";

            File outFile = new File("out.txt");
            bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            
            for (String str : list)
            {
                bout.write(str);
                bout.write("\r\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bin != null)
            {
                try
                {
                    bin.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (bout != null)
            {
                try
                {
                    bout.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return message;
    }
    
    class SortHandler extends Thread
    {
        private List<String> strList = new ArrayList<String>();

        public void add(String str)
        {
            strList.add(str);
        }
        
        @Override
        public void run()
        {
            Collections.sort(strList);
        }
        
        public List<String> list()
        {
            return strList;
        }
    }
}