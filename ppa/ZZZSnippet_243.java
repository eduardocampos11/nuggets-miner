public class NoName {
Date now = new Date();
long timeInterval = now.getTime() - (15705 * 24 * 60 * 60 * 1000L);
long hours = timeInterval / (60 * 60 * 1000L);
LOG.debug(String.format("current date:%s, timeInterval:%d,hours:%d",now.toString(),timeInterval, hours));

}