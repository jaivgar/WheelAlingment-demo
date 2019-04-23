/*
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.qos.drivers;

import eu.arrowhead.common.database.ArrowheadService;
import eu.arrowhead.common.database.ArrowheadSystem;
import eu.arrowhead.common.exception.DriverNotFoundException;
import eu.arrowhead.common.exception.ReservationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class DriversFactory {

  private static DriversFactory instance;

  private Class[] paramVerificationInfo = new Class[1];

  private DriversFactory() {
    //
    super();
    //VerificationInfo parameter
    paramVerificationInfo[0] = ReservationInfo.class;
  }

  /**
   * Returns a instance from this singleton class.
   */
  public static DriversFactory getInstance() {
    if (instance == null) {
      instance = new DriversFactory();
    }
    return instance;
  }


  /**
   * @param networkConfiguration Network configuration parameters on a map.
   * @param provider ArrowheadSystem.
   * @param consumer ArrowheadSystem.
   * @param service ArrowheadService.
   * @param commands Map of the selected commands from the user.
   * @param requestedQoS Map of the desired requestedQoS.
   *
   * @return Returns the generatedCommands from the QoSDriver.
   *
   * @throws ReservationException The StreamConfiguration found an error.
   * @throws DriverNotFoundException The selected type doesnt have an assigned driver.
   */

  public Map<String, String> generateCommands(String communicationProtocol, Map<String, String> networkConfiguration, ArrowheadSystem provider,
                                              ArrowheadSystem consumer, ArrowheadService service, Map<String, String> commands, Map<String, String>
                                                  requestedQoS)
      throws ReservationException, DriverNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

    // Class Invoking
    Class cls = findClass(communicationProtocol);
    Object obj = cls.newInstance();
    // Method Invoking
    Method method = findMethod(cls);

    Map<String, String> streamConfiguration = (Map<String, String>) method.
                                                                              invoke(obj, new ReservationInfo(networkConfiguration, provider, consumer, service, commands, requestedQoS));

    if (streamConfiguration == null) {
      throw new ReservationException();
    }

    return streamConfiguration;

  }

  public Class findClass(String communicationProtocol) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    // Class Invoking
    Class cls;
    cls = Class.forName("eu.arrowhead.qos.communication.drivers." + communicationProtocol.toUpperCase());
    return cls;
  }

  public Method findMethod(Class cls) throws NoSuchMethodException {
    return cls.getDeclaredMethod("reserveQoS", paramVerificationInfo);
  }

}
