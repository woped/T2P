/** modified taken from https://github.com/FabianFriedrich/Text2Process */
package de.dhbw.text2process.models.worldModel;

import de.dhbw.text2process.enums.ConjunctionType;
import de.dhbw.text2process.enums.DLRStatusCode;

public class DetermineConjResult {

  private ConjunctionType f_type;
  private DLRStatusCode f_statusCode;

  /** */
  public DetermineConjResult(ConjunctionType type, DLRStatusCode status) {
    this.setType(type);
    setStatusCode(status);
  }

  public void setType(ConjunctionType type) {
    this.f_type = type;
  }

  public ConjunctionType getType() {
    return f_type;
  }

  public void setStatusCode(DLRStatusCode statusCode) {
    this.f_statusCode = statusCode;
  }

  /**
   * returns a status code to show the kind of relationship 0 - not contained 1 - contained as
   * action 2 - contained as actor (subject) 3 - contained as actor (object) 4 - contained as
   * resource
   *
   * @return
   */
  public DLRStatusCode getStatusCode() {
    return f_statusCode;
  }
}
