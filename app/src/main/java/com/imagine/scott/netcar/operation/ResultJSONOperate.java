package com.imagine.scott.netcar.operation;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.bean.Car;
import com.imagine.scott.netcar.bean.Notification;
import com.imagine.scott.netcar.bean.Order;
import com.imagine.scott.netcar.bean.Province;
import com.imagine.scott.netcar.bean.Region;
import com.imagine.scott.netcar.bean.User;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.bean.VehicleBrand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Scott on 2016/5/22.
 */
public class ResultJSONOperate {

    public static String getRegisterCode(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        return jsonObject.optString("rescode");
    }

    public static int getLoginCode(String jsonStr) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        int resCode = jsonObject.optInt("rescode");
        return resCode;
    }

    public static User getLoginUserInfo(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);

        User user = new User();
        user.setId(Integer.parseInt(jsonObject.optString("id")));
        user.setHeadimage(jsonObject.optString("headimage"));
        user.setPhone(jsonObject.optString("phone"));
        user.setUsername(jsonObject.optString("username"));
        user.setPassword(jsonObject.optString("password"));
        user.setSex(jsonObject.optString("sex"));
        user.setDrivingYears(jsonObject.optString("drivingyears"));
        user.setRegion(jsonObject.optString("region"));
        user.setModifyDate(new Date(Long.parseLong(jsonObject.optString("modifydate"))));
        return user;
    }

    public static List<VehicleBrand> getCarBrandsJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("carbrands");
        List<VehicleBrand> itemVehicleBrands = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            VehicleBrand itemVehicleBrand = new VehicleBrand();
            itemVehicleBrand.setVehicleBrandName(itemData.optString("vehicleBrand"));
            itemVehicleBrand.setVehicleBrandZhName(itemData.optString("vehicleBrandZh"));
            itemVehicleBrands.add(itemVehicleBrand);
        }
        return itemVehicleBrands;
    }

    public static List<Car> getCarModelsJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("cars");
        List<Car> itemVehicleModels = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            Car car = new Car();
            car.setId(itemData.optInt("id"));
            car.setVehicleBrand(itemData.optString("vehiclebrand"));
            car.setVehicleBrandZh(itemData.optString("vehiclebrandzh"));
            car.setVehicleModel(itemData.optString("vehiclemodel"));
            car.setDoorNum(itemData.optInt("doornum"));
            car.setSeatNum(itemData.optInt("seatnum"));
            itemVehicleModels.add(car);
        }
        return itemVehicleModels;
    }

    public static List<UserCar> getUserCarsJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("usercars");
        List<UserCar> usercars = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            UserCar userCar = new UserCar();
            Car car = new Car();
            car.setId(itemData.optInt("carid"));
            car.setVehicleBrand(itemData.optString("vehiclebrand"));
            car.setVehicleBrandZh(itemData.optString("vehiclebrandzh"));
            car.setVehicleModel(itemData.optString("vehiclemodel"));
            car.setDoorNum(itemData.optInt("doornum"));
            car.setSeatNum(itemData.optInt("seatnum"));
            userCar.setCar(car);
            userCar.setId(itemData.optInt("id"));
            userCar.setLicensePlateNumber(itemData.optString("license"));
            userCar.setEngineNum(itemData.optString("enginenum"));
            userCar.setVin(itemData.optString("vin"));
            userCar.setMileage(itemData.optInt("mileage"));
            userCar.setLastMaintainMile(itemData.optInt("lastmaintainmile"));
            userCar.setLampWell(itemData.optBoolean("lampwell"));
            userCar.setEngineWell(itemData.optBoolean("enginewell"));
            userCar.setTransmissionWell(itemData.optBoolean("transmissionwell"));
            userCar.setOilMass(itemData.optInt("oilmass"));
            userCar.setTirePressure(itemData.optBoolean("tirepressure"));
            userCar.setAvgEcon(itemData.optInt("avgecon"));
            userCar.setAirSacSafe(itemData.optBoolean("airsacsafe"));
            usercars.add(userCar);
        }
        return usercars;
    }

    public static List<Province> getProvincesJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("provinces");
        List<Province> itemProvinces = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            Province itemProvince = new Province();
            itemProvince.setId(itemData.optInt("provinceId"));
            itemProvince.setRegionName(itemData.optString("regionName"));
            itemProvinces.add(itemProvince);
        }
        return itemProvinces;
    }

    public static List<Region> getCitysJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("citys");
        List<Region> itemCitys = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            Region city = new Region();
            city.setId(itemData.optInt("id"));
            city.setParentId(itemData.optInt("parentid"));
            city.setRegionName(itemData.optString("regionname"));
            itemCitys.add(city);
        }
        return itemCitys;
    }

    public static List<Order> getOrdersJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("orders");
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            Order order = new Order();
            order.setId(itemData.optInt("id"));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(itemData.optLong("date"));
            order.setDate(c.getTime());
            order.setGasStation(itemData.optString("gasstation"));
            order.setBrandname(itemData.optString("brandname"));
            order.setGasLat(itemData.optDouble("gaslat"));
            order.setGasLng(itemData.optDouble("gaslng"));
            order.setOilType(itemData.optString("oiltype"));
            order.setLitre(itemData.optDouble("litre"));
            order.setMoney(itemData.optDouble("money"));
            orders.add(order);
        }
        return orders;
    }

    public static Order getOrderJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        if (Constants.ADD_ORDER_SUCCESS == jsonObject.optInt("rescode")) {
            Order order = new Order();

            order.setId(jsonObject.optInt("id"));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(jsonObject.optLong("date"));
            order.setDate(c.getTime());
            order.setGasStation(jsonObject.optString("gasstation"));
            order.setBrandname(jsonObject.optString("brandname"));
            order.setGasLat(jsonObject.optDouble("gaslat"));
            order.setGasLng(jsonObject.optDouble("gaslng"));
            order.setOilType(jsonObject.optString("oiltype"));
            order.setLitre(jsonObject.optDouble("litre"));
            order.setMoney(jsonObject.optDouble("money"));
            return order;
        } else {
            return null;
        }
    }

    public static List<Notification> getNotificationsJson(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("notifications");
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemData = jsonArray.getJSONObject(i);
            Notification notification = new Notification();
            UserCar userCar = new UserCar();

            userCar.setId(itemData.optInt("carid"));
            userCar.setLicensePlateNumber(itemData.optString("license"));

            notification.setUserCar(userCar);
            notification.setId(itemData.optInt("id"));
            notification.setTitle(itemData.optString("title"));
            notification.setText(itemData.optString("text"));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(itemData.optLong("date"));
            notification.setDate(c.getTime());
            notifications.add(notification);
        }
        return notifications;
    }
}
