package main;

import database.dao.AbilityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Capitalgame {
    public final Map<String, String> counCapi = new HashMap<>();
    public final List<String> countries = new ArrayList<>();

    public void init(){
        counCapi.put("Украина", "Киев");
        counCapi.put("Афганистан", "Кабул");
        counCapi.put("Польша", "Варшава");
        counCapi.put("Литва", "Вильнюс");
        counCapi.put("Латвия", "Рига");
        counCapi.put("Эстония", "Таллин");
        counCapi.put("Швеция", "Стокгольм");
        counCapi.put("Словения", "Любляна");
        counCapi.put("Молдавия", "Кишинёв");
        counCapi.put("Греция", "Афины");
        counCapi.put("Албания", "Тирана");
        counCapi.put("Чехия", "Прага");
        counCapi.put("Норвегия", "Осло");
        counCapi.put("Вьетнам", "Ханой");
        counCapi.put("Монголия", "Улан-Батор");
        counCapi.put("Ливан", "Бейрут");
        counCapi.put("Ирак", "Багдад");
        counCapi.put("Суринам", "Парамарибо");
        counCapi.put("Демократическая республика Конго", "Киншаса");
        counCapi.put("Беларусь", "Минск");
        counCapi.put("Сербия", "Белград");
        counCapi.put("Соединенные Штаты Америки", "Вашингтон");
        counCapi.put("Канада", "Оттава");
        counCapi.put("Босния и Герцеговина", "Сараево");
        counCapi.put("Хорватия", "Загреб");
        counCapi.put("Германия", "Берлин");
        counCapi.put("Румыния", "Бухарест");
        counCapi.put("Словакия", "Братислава");
        counCapi.put("Ирландия", "Дублин");
        counCapi.put("Замбия", "Лусака");
        counCapi.put("Египет", "Каир");
        counCapi.put("Индия", "Нью-Дели");
        counCapi.put("Северная Македония", "Скопье");
        counCapi.put("Бельгия", "Брюссель");
        counCapi.put("Дания", "Копенгаген");
        counCapi.put("Мальта", "Валетта");
        counCapi.put("Исландия", "Рейкьявик");
        counCapi.put("Финляндия", "Хельсинки");
        counCapi.put("Болгария", "София");
        counCapi.put("Кипр", "Никосия");
        counCapi.put("Монако", "Монако-Виль");
        counCapi.put("Лихтенштейн", "Вадуц");
        counCapi.put("Люксембург", "Люксембург");
        counCapi.put("Швейцария", "Берн");
        counCapi.put("Кения", "Найроби");
        counCapi.put("Иран", "Тегеран");
        counCapi.put("Черногория", "Подгорица");
        counCapi.put("Бразилия", "Бразилиа");
        counCapi.put("Перу", "Лима");
        counCapi.put("Чили", "Сантьяго");
        counCapi.put("Колумбия", "Богота");
        counCapi.put("Аргентина", "Буэнос-Айрес");
        counCapi.put("Боливия", "Сукре");
        counCapi.put("Уругвай", "Монтевидео");
        counCapi.put("Парагвай", "Асунсьон");
        counCapi.put("Аруба", "Ораньестад");
        counCapi.put("Кюрасао", "Виллемстад");
        counCapi.put("Венесуэлла", "Каракас");
        counCapi.put("Французская Гвиана", "Кайенна");
        counCapi.put("Тринидад и Тобаго", "Порт-оф-Спейн");
        counCapi.put("Тайвань", "Тайбэй");


        for(String oneKey: counCapi.keySet()){
            countries.add(oneKey);
        }

    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (chars[i]=='-') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public String getCapital(String country){
        return counCapi.get(country);
    }

    public List<String> getCountries(){
        return countries;
    }

    public String getCountry(int i){
        return countries.get(i);
    }

}
