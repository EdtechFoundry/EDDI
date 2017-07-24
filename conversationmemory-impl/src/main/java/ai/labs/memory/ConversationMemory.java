package ai.labs.memory;

import ai.labs.memory.model.ConversationState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author ginccc
 */
public class ConversationMemory implements IConversationMemory {
    private String id;
    private String botId;
    private Integer botVersion;

    private IWritableConversationStep currentStep;
    private Stack<IConversationStep> previousSteps;
    private Stack<IConversationStep> redoCache = new Stack<>();
    private IConversationMemory.IConversationContext context;
    private ConversationState conversationState;

    ConversationMemory(String id, String botId, Integer botVersion) {
        this(botId, botVersion);
        this.id = id;
    }

    public ConversationMemory(String botId, Integer botVersion) {
        this.botId = botId;
        this.botVersion = botVersion;
        this.context = new ConversationContext();
        this.currentStep = new ConversationStep(context);
        this.previousSteps = new Stack<>();
    }

    @Override
    public IWritableConversationStep getCurrentStep() {
        return currentStep;
    }

    @Override
    public IConversationStepStack getPreviousSteps() {
        return new ConversationStepStack(previousSteps);
    }

    @Override
    public IConversationStepStack getAllSteps() {
        ConversationStepStack result = new ConversationStepStack(previousSteps);
        ((ConversationStep) currentStep).conversationStepNumber = previousSteps.size();
        result.add(currentStep);
        return result;
    }

    public IConversationStep startNextStep() {
        ((ConversationStep) currentStep).conversationStepNumber = previousSteps.size();
        previousSteps.push(currentStep);
        currentStep = new ConversationStep(context);
        return currentStep;
    }

    @Override
    public int size() {
        return previousSteps.size() + 1;
    }

    @Override
    public void undoLastStep() {
        if (!isUndoAvailable()) {
            throw new IllegalStateException();
        }

        redoCache.push(currentStep);
        currentStep = (IWritableConversationStep) previousSteps.pop();
    }

    @Override
    public boolean isUndoAvailable() {
        return previousSteps.size() > 0;
    }

    @Override
    public boolean isRedoAvailable() {
        return redoCache.size() > 0;
    }

    @Override
    public void redoLastStep() {
        if (!isRedoAvailable()) {
            throw new IllegalStateException();
        }

        previousSteps.push(currentStep);
        currentStep = (IWritableConversationStep) redoCache.pop();
    }

    @Override
    public void setCurrentContext(String context) {
        this.context.setContext(context);
    }

    @Override
    public ConversationState getConversationState() {
        return conversationState;
    }

    public void setConversationState(ConversationState conversationState) {
        this.conversationState = conversationState;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getBotId() {
        return botId;
    }

    @Override
    public Integer getBotVersion() {
        return botVersion;
    }

    @Override
    public Stack<IConversationStep> getRedoCache() {
        return redoCache;
    }

    public final class ConversationStepStack implements IConversationStepStack {
        private List<IConversationStep> conversationSteps = new ArrayList<>();

        ConversationStepStack(List<IConversationStep> steps) {
            conversationSteps.addAll(steps);
        }

        @Override
        public <T> IData<T> getLatestData(String key) {
            for (int i = conversationSteps.size() - 1; i >= 0; --i) {
                IConversationStep step = conversationSteps.get(i);
                if (step.getData(key) != null) {
                    return step.getData(key);
                }
            }
            return null;
        }

        @Override
        public <T> List<List<IData<T>>> getAllData(String prefix) {
            List<List<IData<T>>> allData = new LinkedList<>();

            for (int i = conversationSteps.size() - 1; i >= 0; i--) {
                IConversationStep step = conversationSteps.get(i);
                List<IData<T>> dataList = step.getAllData(prefix);
                if (!dataList.isEmpty()) {
                    allData.add(dataList);
                }
            }

            return allData;
        }

        @Override
        public int size() {
            return conversationSteps.size();
        }

        @Override
        public IConversationStep get(int index) {
            return conversationSteps.get(conversationSteps.size() - index - 1);
        }

        @Override
        public IConversationStep peek() {
            return conversationSteps.get(conversationSteps.size() - 1);
        }

        public void addAll(List<IConversationStep> conversationSteps) {
            this.conversationSteps.addAll(conversationSteps);
        }

        public void add(IConversationStep step) {
            this.conversationSteps.add(step);
        }
    }

    public static class ConversationContext implements IConversationContext {
        private String context;

        ConversationContext() {
            this.context = "";
        }

        ConversationContext(String context) {
            this.context = context;
        }

        ConversationContext(IConversationContext context) {
            this.context = context.getContext();
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConversationContext that = (ConversationContext) o;

            return context != null ? context.equals(that.context) : that.context == null;

        }

        @Override
        public int hashCode() {
            return context != null ? context.hashCode() : 0;
        }
    }
}
