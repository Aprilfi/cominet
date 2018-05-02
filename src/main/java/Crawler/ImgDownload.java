package Crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgDownload {

    public static void main(String... args) throws Exception {
        //指定搜索关键字
        String search = "美女";
        //指定搜索开始数据数
        int first = 1;

        String search_url = java.net.URLEncoder.encode(search);

        //创建保存imgsrc的集合
        List<String> imgsrcList = new ArrayList<>();

        for(int j = 0; j <= 3; j ++) {
            //创建字符缓冲流
            StringBuffer sb = new StringBuffer();
            //添加js加载后的网页
            sb.append(new ImgDownload().getHtml("https://cn.bing" +
                    ".com/images/search?q=" + search_url + "&FORM=HDRSC2&first=" + first));
            System.out.println(sb.toString() + "codeLength:" + sb.length());

            //根据源码获取imgurl
            List<String> imgurl = new ImgDownload().getImageUrl(sb.toString());

            //遍历imgurl
            for (String img : imgurl) {
                System.out.println(img);
            }

            String realimgurl = imgurl.get(3);

            List<String> imgsrc = new ImgDownload().getImageSrc(realimgurl);
            int allimg = 0;
            for (int i = 0; i < imgsrc.size(); i++) {
                if (imgsrc.get(i).indexOf("id=") < 40) {
                    allimg++;
                    System.out.println(imgsrc.get(i).substring(0, imgsrc.get(i).indexOf(";")));
                    System.out.println(imgsrc.get(i).indexOf("id"));
                    imgsrcList.add(imgsrc.get(i).substring(0, imgsrc.get(i).indexOf(";")));
                } else {
                    System.out.println("false");
                }
            }
            System.out.println("allimg:  " + allimg);
            first += allimg-1;
            //new ImgDownload().Download(imgsrc,search);
        }
        System.out.println("imgsrclist length : "+ imgsrcList.size());
        for(String src : imgsrcList){
            System.out.println(src);
        }
    }
    /*
     * 获取img标签正则
     */
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    /*
     * 获取src路径的正则
     */
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";

    /**
     * 获取网页源代码
     * @param url 给定的网页路径
     * @return 网页源码字符串
     * @throws Exception
     */
    private String getHtml(String url) throws Exception {
        //指定模拟的浏览器引擎
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
        //配置浏览器配置参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //获取cookie
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        Map<String, String> responseCookies = new HashMap<String, String>();

        for (com.gargoylesoftware.htmlunit.util.Cookie c : cookies) {
            responseCookies.put(c.getName(), c.getValue());
            System.out.print(c.getName()+":"+c.getValue());
        }
        //得到url页面
        HtmlPage page = webClient.getPage(url);
        System.out.println("为了获取js执行的数据 线程开始沉睡等待");
        Thread.sleep(3000);//主要是这个线程的等待 因为js加载也是需要时间的
        System.out.println("线程结束沉睡");
        //URL url1 = new URL(url);
        //打开连接
        //URLConnection urlConnection = url1.openConnection();
        //获取输入流
        InputStream inputStream = page.getWebResponse().getContentAsStream();
        //输入流的包装类
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


        String line;
        StringBuffer sb = new StringBuffer();
        //读取整行
        while((line = bufferedReader.readLine()) != null){
            //添加到stringbuffer
            sb.append(line,0,line.length());
            sb.append("\n");
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        //返回指定url的源代码
        return sb.toString();
    }

    /**
     * 获取源码中imgurl地址
     * @param html url源码
     * @return 保存imgurl的string型集合
     */
    private List<String> getImageUrl(String html){
        //使用正则验证匹配imgurl
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(html);

        List<String>listimgurl = new ArrayList<String>();
        //保存在集合内
        while (matcher.find()){
            listimgurl.add(matcher.group());
        }
        return listimgurl;
    }

    //获取ImageSrc地址
    private List<String> getImageSrc(String listimageurl){
        //创建一个集合
        List<String> listImageSrc=new ArrayList<String>();
            //使用正则验证匹配imgsrc
            Matcher matcher=Pattern.compile(IMGSRC_REG).matcher(listimageurl);
            //imgsrc保存在集合内
            while (matcher.find()){
                    listImageSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        return listImageSrc;
    }

    /**
     * 通过连接保存图片
     * @param listImgSrc 图片src
     */
    private void Download(List<String> listImgSrc,String search) {
        try {
            //开始时间
            Date begindate = new Date();
            int i = 1;
            for (String url : listImgSrc) {

                    //开始时间
                    Date begindate2 = new Date();
                    URL uri = new URL(url);
                    InputStream in = uri.openStream();
                    FileOutputStream fo = new FileOutputStream(new File
                            ("C:\\Users\\Administrator\\Desktop\\img\\" + i + ".png"));
                    byte[] buf = new byte[1024];
                    int length = 0;
                    System.out.println("开始下载:" + url);
                    while ((length = in.read(buf, 0, buf.length)) != -1) {
                        fo.write(buf, 0, length);
                    }
                    in.close();
                    fo.close();
                    System.out.println(i + "下载完成");
                    //结束时间
                    Date overdate2 = new Date();
                    double time = overdate2.getTime() - begindate2.getTime();
                    System.out.println("耗时：" + time / 1000 + "s");
                }
                Date overdate = new Date();
                double time = overdate.getTime() - begindate.getTime();
                System.out.println("总耗时：" + time / 1000 + "s");
                i++;
        } catch (FileNotFoundException e) {
            System.out.println("文件找不到");
        } catch (IOException e) {
            System.out.println("io流异常");
        }
    }

}
