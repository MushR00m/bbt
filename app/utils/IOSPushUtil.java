package utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.transmission.PushQueue;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
 
/**
 * IOS java apns 推送util 王叔叔使用
 * @author luobotao
 * @Date 2015年7月16日
 */
public class IOSPushUtil {
    public static String keystore = null;
    public static String password = null;
    public static String host = null;
    public static Boolean production = true;//true：production false: sandbox
    public static Boolean appStoreFlag = true;//是否是appstore证书 true是   false企业证书
    public static final int numberOfThreads = 20;
    static{
        Properties propertie = new Properties();
        InputStream inputStream;
         
        try {
            inputStream = IOSPushUtil.class.getClassLoader().getResourceAsStream("push.properties");
            propertie.load(inputStream);
            
            appStoreFlag = Boolean.valueOf(propertie.getProperty("appStoreFlag", "true"));
            if(appStoreFlag){
            	keystore = propertie.getProperty("certificatePathAPPStore");
            }else{
            	keystore = propertie.getProperty("certificatePathCompany");
            }
            password = propertie.getProperty("certificatePassword","111111");
            host = propertie.getProperty("host","gateway.push.apple.com");
            inputStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 推送一个简单消息
     * @param msg 消息
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static PushedNotifications pushMsgNotification(String msg,Object devices) throws CommunicationException, KeystoreException{
    	return Push.alert(msg, keystore, password, production, devices);
    }
    /**
     * 推送一个标记
     * @param badge 标记
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static PushedNotifications pushBadgeNotification(int badge,Object devices) throws CommunicationException, KeystoreException{
    	return Push.badge(badge, keystore, password, production, devices);
    }
    /**
     * 推送一个语音
     * @param sound 语音
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushSoundNotification(String sound,Object devices) throws CommunicationException, KeystoreException{
        Push.sound(sound, keystore, password, production, devices);
    }
    /**
     * 推送一个alert+badge+sound通知
     * @param message 消息
     * @param badge 标记
     * @param sound 声音
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static PushedNotifications pushCombinedNotification(String message,int badge,String sound,Object devices) throws CommunicationException, KeystoreException{
    	return Push.combined(message, badge, sound, keystore, password, production, devices);
    }
    /**
     * 通知Apple的杂志内容
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void contentAvailable(Object devices) throws CommunicationException, KeystoreException{
        Push.contentAvailable(keystore, password, production, devices);
    }
    /**
     * 推送有用的调试信息
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void test(Object devices) throws CommunicationException, KeystoreException{
        Push.test(keystore, password, production, devices);
    }
    /**
     * 推送自定义负载
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws JSONException
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static PushedNotifications pushPayload(List<Device> devices, String msg,Integer badge,String sound,Map<String,String> map) throws JSONException, CommunicationException, KeystoreException{
        PushNotificationPayload payload = customPayload(msg, badge, sound, map);
        return Push.payload(payload, keystore, password, production, devices);
        
    }
    /**
     * 用内置线程推送负载信息
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws Exception
     */
    public static PushedNotifications pushPayLoadByThread(List<Device> devices, String msg,Integer badge,String sound,Map<String,String> map) throws Exception{
        PushNotificationPayload payload = customPayload(msg, badge, sound, map);
        return Push.payload(payload, keystore, password, production, numberOfThreads, devices);
    }
    /**
     * 推送配对信息
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws JSONException
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static PushedNotifications pushPayloadDevicePairs(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws JSONException, CommunicationException, KeystoreException{
        List<PayloadPerDevice> payloadDevicePairs = new ArrayList<PayloadPerDevice>();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
            perDevice = new PayloadPerDevice(customPayload(msg+"--->"+i, badge, sound, map), devices.get(i));
            payloadDevicePairs.add(perDevice);
        }
        return Push.payloads(keystore, password, production, payloadDevicePairs);
    }
    /**
     * 用线程推配对信息
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws Exception
     */
    public static PushedNotifications pushPayloadDevicePairsByThread(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws Exception{
        List<PayloadPerDevice> payloadDevicePairs = new ArrayList<PayloadPerDevice>();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
            perDevice = new PayloadPerDevice(customPayload(msg+"--->"+i, badge, sound, map), devices.get(i));
            payloadDevicePairs.add(perDevice);
        }
        return Push.payloads(keystore, password, production,numberOfThreads, payloadDevicePairs);
    }
    /**
     * 队列多线程推送
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws KeystoreException
     * @throws JSONException
     */
    public static void queue(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws KeystoreException, JSONException{
        PushQueue queue = Push.queue(keystore, password, production, numberOfThreads);
        queue.start();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
        	try {
        		perDevice = new PayloadPerDevice(customPayload(msg, badge, sound, map), devices.get(i));
                queue.add(perDevice);
			} catch (Exception e) {
				e.printStackTrace();
			}
            
        }
    }
    /**
     * 自定义负载
     * @param msg
     * @param badge
     * @param sound
     * @param map 自定义字典
     * @return
     * @throws JSONException
     */
    private static PushNotificationPayload customPayload(String msg,Integer badge,String sound,Map<String,String> map) throws JSONException{
        PushNotificationPayload payload = PushNotificationPayload.complex();
        if(StringUtils.isNotEmpty(msg)){
            payload.addAlert(msg);         
        }
        if(badge != null){         
            payload.addBadge(badge);
        }
        payload.addSound(StringUtils.defaultIfEmpty(sound, "default"));
        if(map!=null && !map.isEmpty()){
            Object[] keys = map.keySet().toArray();    
            Object[] vals = map.values().toArray();
            if(keys!= null && vals != null && keys.length == vals.length){
                for (int i = 0; i < map.size(); i++) {                  
                    payload.addCustomDictionary(String.valueOf(keys[i]),String.valueOf(vals[i]));
                }
            }
        }
        return payload;
    }
    
    public static void main(String[] args) throws Exception {
    	String token="1753e7ca 9f3d1dca a6b2d352 3159fa68 c76d1bd4 d56ba9e9 ef796f9d eb643584";
//    	String token2="0afdc415 6ca4ac82 556be8c4 c9147953 a3be0976 ce661765 97fb8322 e54a6ac9";
//    	String token3="43c9673e 44838708 447e2a11 7b94da1e 06434bcd 49476da8 56038011 166c08ce";
    	token = token.replaceAll(" ", "");
//    	token2 = token2.replaceAll(" ", "");
//    	token3 = token3.replaceAll(" ", "");
//        pushMsgNotification("hello!你好啊，哈哈",  "ef6c309527ce850ea6fe7d108b097468fae0742a0c1a284d0de75d9f4da90fd0");
//        pushMsgNotification("hello!你好要啊，哈哈",  "108150cdcc112af510ca26b03da9923d86c301abcff06605c55ffbb2d068049c");
//        pushMsgNotification("h 基本原则ello!你好要啊，哈哈",  token);
    	List<String> tokenList = new ArrayList<String>();
    	tokenList.add(token);
//    	tokenList.add(token2);
//    	tokenList.add(token3);
    	String alert = "我的push测试";//push的内容
        int badge = 1;//图标小红圈的数值
        String sound = "default";//铃音
    	 PushNotificationPayload payLoad = new PushNotificationPayload();
         payLoad.addAlert(alert); // 消息内容
         payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值
         if (!StringUtils.isBlank(sound))
         {
             payLoad.addSound(sound);//铃音
         }
         pushCombinedNotification(alert, badge, sound, token);
         Map<String,String> map = new HashMap<String, String>();
         List<Device> devices = Devices.asDevices(tokenList);
         map.put("url", "pDe://pid=1218");
         PushedNotifications  result = pushPayload(devices, alert, badge, sound, map);
         for(PushedNotification temp :result){
        	 System.out.println(temp.isSuccessful()+"and the token is:"+temp.getDevice().getToken());
         }
//        pushMsgNotification("pDe://pid=3502",  token);
//        pushMsgNotification("我发哈哈",  token2);
//        pushMsgNotification("我发哈哈",  token3);
//        pushBadgeNotification(1,  token);
//        pushMsgNotification("h 基本原则ello!你好要啊，哈哈",  token3);
//        pushBadgeNotification(122,  token);
//        pushBadgeNotification(144,  token3);
         /*String[] devs= new String[10000];
     	for (int i = 0; i < devs.length; i++) {
        	devs[i] = "iostoken";
        }
      List<Device> devices=Devices.asDevices("ef6c309527ce850ea6fe7d108b097468fae0742a0c1a284d0de75d9f4da90fd0");
        System.out.println(devices.size());
        //pushPayLoadByThread(devices, "Hello 2222222", 1, null, null);
        //pushPayloadDevicePairs(devices, "Hello 111111111", 1, null, null);
        //pushPayloadDevicePairs(devices, "Hello +++", 1, null, null);
        queue(devices,"Hello 2222222", 1, null, null);*/
    }
}