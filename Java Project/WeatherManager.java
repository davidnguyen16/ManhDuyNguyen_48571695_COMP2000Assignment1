import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WeatherManager {
    private List<Weather> activeWeatherEffects;

    public WeatherManager() {
        this.activeWeatherEffects = new ArrayList<Weather>();
    }

    public void addWeather(Weather weather) {
        if (weather == null) {
            throw new IllegalArgumentException("Weather cannot be null");
        }
        if (!weather.isActive()) {
            throw new IllegalArgumentException("Weather must have remaining duration");
        }
        if (this.activeWeatherEffects.contains(weather)) {
            throw new IllegalArgumentException("This weather object is already active");
        }
        this.activeWeatherEffects.add(weather);
    }

    public void clearWeather() {
        this.activeWeatherEffects.clear();
    }

    /** Copies the list structure; the Weather objects themselves are shared. */
    public List<Weather> getActiveWeatherEffects() {
        return new ArrayList<Weather>(this.activeWeatherEffects);
    }

    /** Applies effects in insertion order and safely removes expired ones. */
    public void update(ForestFireSimulation simulation) {
        if (simulation == null) {
            throw new IllegalArgumentException("Simulation cannot be null");
        }
        Iterator<Weather> iterator = this.activeWeatherEffects.iterator();
        while (iterator.hasNext()) {
            Weather weather = iterator.next();
            weather.update(simulation);
            if (!weather.isActive()) {
                iterator.remove();
            }
        }
    }
}
