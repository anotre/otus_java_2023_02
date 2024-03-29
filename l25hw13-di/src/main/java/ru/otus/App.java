package ru.otus;

import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.config.AppConfig;
import ru.otus.config.AppConfig1;
import ru.otus.config.AppConfig2;
import ru.otus.services.GameProcessor;
import ru.otus.services.GameProcessorImpl;

public class App {

    public static void main(String[] args) throws Exception {
        // Опциональные варианты
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);

        // Тут можно использовать библиотеку Reflections (см. зависимости)
//        AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");

        // Обязательный вариант
//        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);

        // Приложение должно работать в каждом из указанных ниже вариантов
//        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
//        GameProcessor gameProcessor = container.getAppComponent(GameProcessorImpl.class);
        GameProcessor gameProcessor = container.getAppComponent("gameProcessor");

        gameProcessor.startGame();
    }
}
