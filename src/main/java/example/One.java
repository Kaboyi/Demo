package example;

import sun.security.provider.SHA;

import java.io.*;
import java.util.*;

/**
 * @author Administrator
 */
public class One
{
    private static final String IN_FILE = "in.txt";
    
    private static final String OUT_FILE = "out.txt";
    
    String message = "foo";
    
    public String foo()
    {
        BufferedReader bin = null;
        BufferedWriter bout = null;
        try
        {
            File inFile = new File(IN_FILE);
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            
            // 第一行是数字,代表线程数
            String firstLine = bin.readLine();
            Integer processNum = Integer.valueOf(firstLine);
            
            // 线程池
            Map<Integer, SortHandler> threadMap = new HashMap<Integer, SortHandler>(1 << 5);
            
            // 读取字符行,放入线程池中
            readLine(bin, processNum, threadMap);
            
            // 计时
            long st = System.currentTimeMillis();

            // 运行线程
            run(threadMap);

            // 合并
            List<String> list = merge(threadMap);
            
            System.out.println("Time : " + (System.currentTimeMillis() - st));
            
            message = "Sorted the data";
            
            File outFile = new File(OUT_FILE);
            bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            
            output(bout, list);
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

    private void readLine(BufferedReader bin, Integer processNum, Map<Integer, SortHandler> threadMap) throws IOException {
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
    }

    private void run(Map<Integer, SortHandler> threadMap)
    {
        for (Thread thread : threadMap.values())
        {
            thread.start();
        }
        for (Thread thread : threadMap.values())
        {
            try
            {
                thread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void output(BufferedWriter bout, List<String> list)
        throws IOException
    {
        
        for (String str : list)
        {
            bout.write(str);
            bout.write("\r\n");
        }
    }
    
    private List<String> merge(Map<Integer, SortHandler> threadMap)
    {
        // 归并结果
        List<String> list = new ArrayList();
        for (SortHandler rs : threadMap.values())
        {
            List<String> list1 = rs.list();
            list.addAll(list1);
        }
        
        // 排序
        Collections.sort(list);
        return list;
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