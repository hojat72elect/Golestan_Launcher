import React, {useEffect, useState} from 'react';
import {Dimensions, FlatList, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {launchApp} from './src/modules/LauncherModule';
import type {AppInfo} from './src/domain/AppInfo';
import {GetInstalledAppsUseCase} from "./src/usecases/GetInstalledAppsUseCase";

const {width} = Dimensions.get('window');
const numColumns = 4;

export default function App() {
    const [apps, setApps] = useState<AppInfo[]>([]);
    const [filteredApps, setFilteredApps] = useState<AppInfo[]>([]);
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        loadApps();
    }, []);

    useEffect(() => {
        if (searchQuery.trim() === '') {
            setFilteredApps(apps);
        } else {
            const filtered = apps.filter(app =>
                app.appName.toLowerCase().includes(searchQuery.toLowerCase())
            );
            setFilteredApps(filtered);
        }
    }, [searchQuery, apps]);

    const loadApps = async () => {
        const installedApps = await GetInstalledAppsUseCase();
        setApps(installedApps);
        setFilteredApps(installedApps);
    };

    const handleAppPress = async (app: AppInfo) => {
        await launchApp(app.packageName, app.activityName);
    };

    const renderAppItem = ({item}: { item: AppInfo }) => (
        <TouchableOpacity
            style={styles.appItem}
            onPress={() => handleAppPress(item)}
            activeOpacity={0.7}
        >
            <View style={styles.appIcon}>
                <Text style={styles.appIconText}>{item.appName.charAt(0)}</Text>
            </View>
            <Text style={styles.appName} numberOfLines={2}>
                {item.appName}
            </Text>
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            <StatusBar hidden={true}/>
            <View style={styles.searchContainer}>
                <TextInput
                    style={styles.searchInput}
                    placeholder="Search apps..."
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    placeholderTextColor="#999"
                />
            </View>
            <FlatList
                data={filteredApps}
                renderItem={renderAppItem}
                keyExtractor={(item) => item.packageName}
                numColumns={numColumns}
                contentContainerStyle={styles.gridContainer}
                columnWrapperStyle={styles.row}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    },
    header: {
        backgroundColor: '#6200ee',
        paddingVertical: 20,
        paddingHorizontal: 16,
        paddingTop: 40,
    },
    searchContainer: {
        padding: 16,
        backgroundColor: '#fff',
        elevation: 2,
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
    },
    searchInput: {
        backgroundColor: '#f0f0f0',
        borderRadius: 8,
        padding: 12,
        fontSize: 16,
        color: '#333',
    },
    gridContainer: {
        padding: 8,
    },
    row: {
        justifyContent: 'space-between',
    },
    appItem: {
        width: (width - 32) / numColumns - 8,
        alignItems: 'center',
        margin: 4,
        padding: 8,
    },
    appIcon: {
        width: 56,
        height: 56,
        borderRadius: 12,
        backgroundColor: '#6200ee',
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: 8,
    },
    appIconText: {
        color: '#fff',
        fontSize: 24,
        fontWeight: 'bold',
    },
    appName: {
        fontSize: 12,
        color: '#333',
        textAlign: 'center',
        fontWeight: '500',
    },
});
