package io;

/**
 * Profile class for GPIOConnection
 */
public class GPIOProfile implements ConnectionProfile {

    /**
     * Get connection profile
     * @return connection profile string
     */
    public String getProfile() {
        return config;
    }

    /**
     * Set GPIO configuration as String like XXIIIOOOOO<br>
     * X = not used<br>
     * I = input<br>
     * O = output<br>
     * @param config String like GPIO configuration<br>
     * TC65 has 10 GPIO's => max config length = 10<br>
     */
    public void setGPIOConfiguration(String config) {
        if (config.length() > 10) {
            this.config = config.substring(0, 10);
        } else {
            this.config = config;
        }
    }
    private String config = "XXXXXXXXXX";

    /**
     * Get GPIO configuration string where X=not used, I=input, O=output
     * @return GPIO configuration as String like XXIIIOOOOO
     */
    public String getConfig() {
        return config;
    }

    /**
     * Get total number of input pins in configuration
     * @return total input pins in confiuration
     */
    public int getInputPinCount() {
        int input_size = 0;
        for (int i = 0; i < config.length(); i++) {
            if (config.charAt(i) == 'I') {
                input_size++;
            }
        }
        return input_size;
    }

    /**
     * Get total number of output pins in configuration
     * @return  total output pins in confiuration
     */
    public int getOutputPinCount() {
        int output_size = 0;
        for (int i = 0; i < config.length(); i++) {
            if (config.charAt(i) == 'O') {
                output_size++;
            }
        }
        return output_size;
    }

    /**
     * Set output port init state
     * @param outputState output state
     */
    public void setDefaultOutputState(int outputState) {
        this.output_state = outputState;
    }
    protected int output_state = 0;

    /**
     * Set a GPIO event listener<br>
     * <b>OBS: </b>Listener must be started before opening the connection
     * @param gpioEvent GPIOEvent implementation
     * @param pollRate polling rate in miliseconds
     */
    public void setGPIOListener(GPIOEvent gpioEvent, int pollRate) {
        this.gpio_event = gpioEvent;
        this.poll_rate_ms = pollRate;
    }
    protected GPIOEvent gpio_event = null;
    protected int poll_rate_ms;

    /**
     * GPIO event interface.
     */
    public static interface GPIOEvent {

        /**
         * Input pin/port event
         * @param value
         */
        public void eventInput(int value);

        /**
         * Output pin/port event
         * @param value
         */
        public void eventOutput(int value);
    }
}
